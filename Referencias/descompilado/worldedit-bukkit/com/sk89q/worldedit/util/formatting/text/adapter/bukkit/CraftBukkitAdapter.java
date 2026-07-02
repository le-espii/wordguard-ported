/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializer
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.sk89q.worldedit.util.formatting.text.adapter.bukkit;

import com.google.gson.JsonDeserializer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.Adapter;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

final class CraftBukkitAdapter
implements Adapter {
    private static final Binding REFLECTION_BINDINGS = CraftBukkitAdapter.load();
    private static final boolean ALIVE = REFLECTION_BINDINGS.valid();

    CraftBukkitAdapter() {
    }

    private static Binding load() {
        try {
            Constructor<?> titlePacketConstructor;
            Object titleActionActionBar;
            Constructor<?> chatPacketConstructor;
            Class<?> server = Bukkit.getServer().getClass();
            if (!CraftBukkitAdapter.isCompatibleServer(server)) {
                throw new UnsupportedOperationException("Incompatible server version");
            }
            String serverVersion = CraftBukkitAdapter.maybeVersion(server.getPackage().getName().substring("org.bukkit.craftbukkit".length()));
            Class<?> craftPlayerClass = CraftBukkitAdapter.craftBukkitClass(serverVersion, "entity.CraftPlayer");
            Method getHandleMethod = craftPlayerClass.getMethod("getHandle", new Class[0]);
            Class<?> entityPlayerClass = getHandleMethod.getReturnType();
            Field playerConnectionField = entityPlayerClass.getField("playerConnection");
            Class<?> playerConnectionClass = playerConnectionField.getType();
            Class<?> packetClass = CraftBukkitAdapter.minecraftClass(serverVersion, "Packet");
            Method sendPacketMethod = playerConnectionClass.getMethod("sendPacket", packetClass);
            Class<?> baseComponentClass = CraftBukkitAdapter.minecraftClass(serverVersion, "IChatBaseComponent");
            Class<?> chatMessageTypeClass = CraftBukkitAdapter.optionalMinecraftClass(serverVersion, "ChatMessageType");
            Object chatMessageTypeSystem = CraftBukkitAdapter.enumValue(chatMessageTypeClass, "SYSTEM", 1);
            Class<?> chatPacketClass = CraftBukkitAdapter.minecraftClass(serverVersion, "PacketPlayOutChat");
            boolean longConstructor = false;
            try {
                chatPacketConstructor = chatPacketClass.getConstructor(baseComponentClass);
            }
            catch (NoSuchMethodException ex) {
                if (chatMessageTypeClass == null) {
                    throw ex;
                }
                longConstructor = true;
                chatPacketConstructor = chatPacketClass.getConstructor(baseComponentClass, chatMessageTypeClass, UUID.class);
            }
            Constructor<?> legacyChatPacketConstructor = CraftBukkitAdapter.optionalConstructor(chatPacketClass, baseComponentClass, Byte.TYPE);
            Class<?> titlePacketClass = CraftBukkitAdapter.optionalMinecraftClass(serverVersion, "PacketPlayOutTitle");
            if (titlePacketClass != null) {
                Class<?> titlePacketClassAction = CraftBukkitAdapter.minecraftClass(serverVersion, "PacketPlayOutTitle$EnumTitleAction");
                titleActionActionBar = CraftBukkitAdapter.enumValue(titlePacketClassAction, "ACTIONBAR", Integer.MAX_VALUE);
                titlePacketConstructor = titlePacketClass.getConstructor(titlePacketClassAction, baseComponentClass);
            } else {
                titleActionActionBar = null;
                titlePacketConstructor = null;
            }
            Class chatSerializerClass = Arrays.stream(baseComponentClass.getClasses()).filter(JsonDeserializer.class::isAssignableFrom).findAny().orElseGet(() -> {
                try {
                    return CraftBukkitAdapter.minecraftClass(serverVersion, "ChatSerializer");
                }
                catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            Method serializeMethod = Arrays.stream(chatSerializerClass.getMethods()).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> baseComponentClass.isAssignableFrom(m.getReturnType())).filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(String.class)).min(Comparator.comparing(Method::getName)).orElseThrow(() -> new RuntimeException("Unable to find serialize method"));
            return new AliveBinding(getHandleMethod, playerConnectionField, sendPacketMethod, chatMessageTypeSystem, legacyChatPacketConstructor, chatPacketConstructor, titleActionActionBar, titlePacketConstructor, serializeMethod, longConstructor);
        }
        catch (Throwable e) {
            return new DeadBinding();
        }
    }

    private static boolean isCompatibleServer(Class<?> serverClass) {
        return serverClass.getPackage().getName().startsWith("org.bukkit.craftbukkit") && serverClass.getSimpleName().equals("CraftServer");
    }

    private static Class<?> craftBukkitClass(String version, String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + version + name);
    }

    private static Class<?> minecraftClass(String version, String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + version + name);
    }

    private static String maybeVersion(String version) {
        if (version.isEmpty()) {
            return "";
        }
        if (version.charAt(0) == '.') {
            return version.substring(1) + '.';
        }
        throw new IllegalArgumentException("Unknown version " + version);
    }

    private static Class<?> optionalMinecraftClass(String version, String name) {
        try {
            return CraftBukkitAdapter.minecraftClass(version, name);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public void sendMessage(List<? extends CommandSender> viewers, Component component) {
        if (!ALIVE) {
            return;
        }
        CraftBukkitAdapter.send(viewers, component, REFLECTION_BINDINGS::createMessagePacket);
    }

    @Override
    public void sendActionBar(List<? extends CommandSender> viewers, Component component) {
        if (!ALIVE) {
            return;
        }
        CraftBukkitAdapter.send(viewers, component, REFLECTION_BINDINGS::createActionBarPacket);
    }

    private static void send(List<? extends CommandSender> viewers, Component component, Function<Component, Object> function) {
        Object packet = null;
        Iterator<? extends CommandSender> iterator = viewers.iterator();
        while (iterator.hasNext()) {
            CommandSender sender = iterator.next();
            if (!(sender instanceof Player)) continue;
            try {
                Player player = (Player)sender;
                if (packet == null) {
                    packet = function.apply(component);
                }
                REFLECTION_BINDINGS.sendPacket(packet, player);
                iterator.remove();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static @Nullable Object enumValue(@Nullable Class<?> klass, String name, int ordinal) {
        if (klass == null) {
            return null;
        }
        if (!Enum.class.isAssignableFrom(klass)) {
            return null;
        }
        try {
            return Enum.valueOf(klass.asSubclass(Enum.class), name);
        }
        catch (IllegalArgumentException ex) {
            ?[] constants = klass.getEnumConstants();
            if (constants.length > ordinal) {
                return constants[ordinal];
            }
            return null;
        }
    }

    private static <T> @Nullable Constructor<T> optionalConstructor(@Nullable Class<T> klass, Class<?> ... args) {
        if (klass == null) {
            return null;
        }
        try {
            return klass.getConstructor(args);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static final class AliveBinding
    extends Binding {
        private static final UUID NIL_UUID = new UUID(0L, 0L);
        private static final byte LEGACY_CHAT_MESSAGE_TYPE_ACTION_BAR = 2;
        private final Method getHandleMethod;
        private final Field playerConnectionField;
        private final Method sendPacketMethod;
        private final Object chatMessageTypeSystem;
        private final Constructor<?> legacyChatPacketConstructor;
        private final Constructor<?> chatPacketConstructor;
        private final Object titlePacketActionActionBar;
        private final Constructor<?> titlePacketConstructor;
        private final boolean canMakeTitle;
        private final Method serializeMethod;
        private final boolean longChatPacketConstructor;

        AliveBinding(Method getHandleMethod, Field playerConnectionField, Method sendPacketMethod, Object chatMessageTypeSystem, Constructor<?> legacyChatPacketConstructor, Constructor<?> chatPacketConstructor, Object titlePacketActionActionBar, Constructor<?> titlePacketConstructor, Method serializeMethod, boolean longChatPacketConstructor) {
            this.getHandleMethod = getHandleMethod;
            this.playerConnectionField = playerConnectionField;
            this.sendPacketMethod = sendPacketMethod;
            this.chatMessageTypeSystem = chatMessageTypeSystem;
            this.legacyChatPacketConstructor = legacyChatPacketConstructor;
            this.chatPacketConstructor = chatPacketConstructor;
            this.titlePacketConstructor = titlePacketConstructor;
            this.titlePacketActionActionBar = titlePacketActionActionBar;
            this.canMakeTitle = this.titlePacketConstructor != null && this.titlePacketActionActionBar != null;
            this.serializeMethod = serializeMethod;
            this.longChatPacketConstructor = longChatPacketConstructor;
        }

        @Override
        boolean valid() {
            return true;
        }

        @Override
        Object createMessagePacket(Component component) {
            String json = GsonComponentSerializer.INSTANCE.serialize(component);
            try {
                Object mc = this.serializeMethod.invoke(null, json);
                if (this.longChatPacketConstructor) {
                    return this.chatPacketConstructor.newInstance(mc, this.chatMessageTypeSystem, NIL_UUID);
                }
                return this.chatPacketConstructor.newInstance(mc);
            }
            catch (Exception e) {
                throw new UnsupportedOperationException("An exception was encountered while creating a packet for a component", e);
            }
        }

        @Override
        Object createActionBarPacket(Component component) {
            if (this.canMakeTitle) {
                try {
                    String json = GsonComponentSerializer.INSTANCE.serialize(component);
                    return this.titlePacketConstructor.newInstance(this.titlePacketActionActionBar, this.serializeMethod.invoke(null, json));
                }
                catch (Exception e) {
                    throw new UnsupportedOperationException("An exception was encountered while creating a packet for a component", e);
                }
            }
            if (this.legacyChatPacketConstructor != null) {
                String legacy = LegacyComponentSerializer.legacy().serialize(component);
                String json = GsonComponentSerializer.INSTANCE.serialize(TextComponent.of(legacy));
                try {
                    return this.legacyChatPacketConstructor.newInstance(this.serializeMethod.invoke(null, json), (byte)2);
                }
                catch (Exception e) {
                    throw new UnsupportedOperationException("An exception was encountered while creating a packet for a component", e);
                }
            }
            return this.createMessagePacket(component);
        }

        @Override
        void sendPacket(Object packet, Player player) {
            try {
                Object connection = this.playerConnectionField.get(this.getHandleMethod.invoke(player, new Object[0]));
                this.sendPacketMethod.invoke(connection, packet);
            }
            catch (Exception e) {
                throw new UnsupportedOperationException("An exception was encountered while sending a packet for a component", e);
            }
        }
    }

    private static final class DeadBinding
    extends Binding {
        private DeadBinding() {
        }

        @Override
        boolean valid() {
            return false;
        }

        @Override
        Object createMessagePacket(Component component) {
            throw new UnsupportedOperationException();
        }

        @Override
        Object createActionBarPacket(Component component) {
            throw new UnsupportedOperationException();
        }

        @Override
        void sendPacket(Object packet, Player player) {
            throw new UnsupportedOperationException();
        }
    }

    private static abstract class Binding {
        private Binding() {
        }

        abstract boolean valid();

        abstract Object createMessagePacket(Component var1);

        abstract Object createActionBarPacket(Component var1);

        abstract void sendPacket(Object var1, Player var2);
    }
}

