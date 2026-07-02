/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.internal.command;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import java.util.List;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.gen.CommandCallListener;
import org.enginehub.piston.gen.CommandRegistration;

public class CommandRegistrationHandler {
    private static final CommandPermissionsConditionGenerator PERM_GEN = new CommandPermissionsConditionGenerator();
    private final List<CommandCallListener> callListeners;

    public CommandRegistrationHandler(List<CommandCallListener> callListeners) {
        this.callListeners = ImmutableList.copyOf(callListeners);
    }

    public <CI> void register(CommandManager manager, CommandRegistration<CI> registration, CI instance) {
        registration.containerInstance(instance).commandManager(manager).listeners(this.callListeners);
        if (registration instanceof CommandPermissionsConditionGenerator.Registration) {
            ((CommandPermissionsConditionGenerator.Registration)((Object)registration)).commandPermissionsConditionGenerator(PERM_GEN);
        }
        registration.build();
    }
}

