/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.plugin.Plugin
 */
package com.sk89q.bukkit.util;

import com.sk89q.bukkit.util.CommandInfo;
import com.sk89q.bukkit.util.CommandRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandsManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

@Deprecated
public class CommandsManagerRegistration
extends CommandRegistration {
    protected CommandsManager<?> commands;

    public CommandsManagerRegistration(Plugin plugin, CommandsManager<?> commands) {
        super(plugin);
        this.commands = commands;
    }

    public CommandsManagerRegistration(Plugin plugin, CommandExecutor executor, CommandsManager<?> commands) {
        super(plugin, executor);
        this.commands = commands;
    }

    public boolean register(Class<?> clazz) {
        return this.registerAll(this.commands.registerAndReturn(clazz));
    }

    public boolean registerAll(List<Command> registered) {
        ArrayList<CommandInfo> toRegister = new ArrayList<CommandInfo>();
        for (Command command : registered) {
            List<String> permissions = null;
            Method cmdMethod = this.commands.getMethods().get(null).get(command.aliases()[0]);
            Map<String, Method> childMethods = this.commands.getMethods().get(cmdMethod);
            if (cmdMethod != null && cmdMethod.isAnnotationPresent(CommandPermissions.class)) {
                permissions = Arrays.asList(cmdMethod.getAnnotation(CommandPermissions.class).value());
            } else if (cmdMethod != null && childMethods != null && !childMethods.isEmpty()) {
                permissions = new ArrayList<String>();
                for (Method m : childMethods.values()) {
                    if (!m.isAnnotationPresent(CommandPermissions.class)) continue;
                    permissions.addAll(Arrays.asList(m.getAnnotation(CommandPermissions.class).value()));
                }
            }
            toRegister.add(new CommandInfo(command.usage(), command.desc(), command.aliases(), this.commands, permissions == null ? null : permissions.toArray(new String[permissions.size()])));
        }
        return ((CommandRegistration)this).register(toRegister);
    }
}

