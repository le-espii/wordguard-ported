/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.internal.Excluder
 *  com.google.gson.reflect.TypeToken
 *  net.md_5.bungee.api.ChatMessageType
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.chat.ComponentSerializer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.sk89q.worldedit.util.formatting.text.adapter.bukkit;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.Adapter;
import com.sk89q.worldedit.util.formatting.text.serializer.gson.GsonComponentSerializer;
import com.sk89q.worldedit.util.formatting.text.serializer.legacy.LegacyComponentSerializer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class SpigotAdapter
implements Adapter {
    private static final boolean BOUND = SpigotAdapter.bind();

    SpigotAdapter() {
    }

    private static boolean bind() {
        try {
            Class<?> treeTypeAdapterClass;
            Field gsonField = SpigotAdapter.field(ComponentSerializer.class, "gson");
            Field factoriesField = SpigotAdapter.field(Gson.class, "factories");
            Field builderFactoriesField = SpigotAdapter.field(GsonBuilder.class, "factories");
            Field builderHierarchyFactoriesField = SpigotAdapter.field(GsonBuilder.class, "hierarchyFactories");
            Gson gson = (Gson)gsonField.get(null);
            GsonBuilder builder = GsonComponentSerializer.populate(new GsonBuilder());
            List existingFactories = (List)factoriesField.get(gson);
            ArrayList newFactories = new ArrayList();
            newFactories.addAll((List)builderFactoriesField.get(builder));
            Collections.reverse(newFactories);
            newFactories.addAll((List)builderHierarchyFactoriesField.get(builder));
            ArrayList<TypeAdapterFactory> modifiedFactories = new ArrayList<TypeAdapterFactory>(existingFactories);
            int index = SpigotAdapter.findExcluderIndex(modifiedFactories);
            for (TypeAdapterFactory newFactory : Lists.reverse(newFactories)) {
                modifiedFactories.add(index, newFactory);
            }
            try {
                treeTypeAdapterClass = Class.forName("com.google.gson.internal.bind.TreeTypeAdapter");
            }
            catch (ClassNotFoundException e) {
                treeTypeAdapterClass = Class.forName("com.google.gson.TreeTypeAdapter");
            }
            Method newFactoryWithMatchRawTypeMethod = treeTypeAdapterClass.getMethod("newFactoryWithMatchRawType", TypeToken.class, Object.class);
            TypeAdapterFactory adapterComponentFactory = (TypeAdapterFactory)newFactoryWithMatchRawTypeMethod.invoke(null, TypeToken.get(AdapterComponent.class), new Serializer());
            modifiedFactories.add(index, adapterComponentFactory);
            factoriesField.set(gson, modifiedFactories);
            return true;
        }
        catch (Throwable e) {
            return false;
        }
    }

    private static Field field(Class<?> klass, String name) throws NoSuchFieldException {
        Field field = klass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static int findExcluderIndex(List<TypeAdapterFactory> factories) {
        int size = factories.size();
        for (int i = 0; i < size; ++i) {
            TypeAdapterFactory factory = factories.get(i);
            if (!(factory instanceof Excluder)) continue;
            return i + 1;
        }
        return 0;
    }

    @Override
    public void sendMessage(List<? extends CommandSender> viewers, Component component) {
        if (!BOUND) {
            return;
        }
        SpigotAdapter.send(viewers, component, (viewer, components) -> viewer.spigot().sendMessage(components));
    }

    @Override
    public void sendActionBar(List<? extends CommandSender> viewers, Component component) {
        if (!BOUND) {
            return;
        }
        SpigotAdapter.send(viewers, component, (viewer, components) -> viewer.spigot().sendMessage(ChatMessageType.ACTION_BAR, components));
    }

    private static void send(List<? extends CommandSender> viewers, Component component, BiConsumer<Player, BaseComponent[]> consumer) {
        if (!BOUND) {
            return;
        }
        BaseComponent[] components = new BaseComponent[]{new AdapterComponent(component)};
        Iterator<? extends CommandSender> it = viewers.iterator();
        while (it.hasNext()) {
            CommandSender viewer = it.next();
            if (!(viewer instanceof Player)) continue;
            try {
                consumer.accept((Player)viewer, components);
                it.remove();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    static BaseComponent[] toBungeeCord(Component component) {
        if (BOUND) {
            return new BaseComponent[]{new AdapterComponent(component)};
        }
        return ComponentSerializer.parse((String)GsonComponentSerializer.INSTANCE.serialize(component));
    }

    public static final class AdapterComponent
    extends BaseComponent {
        private final Component component;

        AdapterComponent(Component component) {
            this.component = component;
        }

        public BaseComponent duplicate() {
            return this;
        }

        public String toLegacyText() {
            return LegacyComponentSerializer.legacy().serialize(this.component);
        }
    }

    public static class Serializer
    implements JsonSerializer<AdapterComponent> {
        public JsonElement serialize(AdapterComponent src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize((Object)src.component);
        }
    }
}

