/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.service.AutoService
 */
package org.enginehub.piston.impl;

import com.google.auto.service.AutoService;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.CommandManagerService;
import org.enginehub.piston.impl.CommandImpl;
import org.enginehub.piston.impl.CommandManagerImpl;

@AutoService(value={CommandManagerService.class})
public class CommandManagerServiceImpl
implements CommandManagerService {
    @Override
    public String id() {
        return "default-impl";
    }

    @Override
    public CommandManager newCommandManager() {
        return new CommandManagerImpl();
    }

    @Override
    public Command.Builder newCommand(String name) {
        return CommandImpl.builder(name);
    }
}

