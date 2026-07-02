/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.gen;

import java.util.Collection;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.gen.CommandCallListener;

public interface CommandRegistration<CI> {
    public CommandRegistration<CI> commandManager(CommandManager var1);

    public CommandRegistration<CI> containerInstance(CI var1);

    public CommandRegistration<CI> listeners(Collection<CommandCallListener> var1);

    public void build();
}

