/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package org.enginehub.piston;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import org.enginehub.piston.ArgBinding;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandParameters;

public interface CommandParseResult {
    public ImmutableList<Command> getExecutionPath();

    default public Command getPrimaryCommand() {
        return (Command)Iterables.getLast(this.getExecutionPath());
    }

    public ImmutableList<ArgBinding> getBoundArguments();

    default public List<String> getOriginalArguments() {
        return Lists.transform(this.getBoundArguments(), ArgBinding::getInput);
    }

    public CommandParameters getParameters();
}

