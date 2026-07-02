/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston;

import org.enginehub.piston.Command;
import org.enginehub.piston.CommandManager;

public interface CommandManagerService {
    public String id();

    public CommandManager newCommandManager();

    public Command.Builder newCommand(String var1);
}

