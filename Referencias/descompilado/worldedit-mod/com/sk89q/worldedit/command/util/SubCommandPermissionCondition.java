/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package com.sk89q.worldedit.command.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.command.util.PermissionCondition;
import java.lang.invoke.LambdaMetafactory;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.enginehub.piston.Command;
import org.enginehub.piston.inject.InjectedValueAccess;

public final class SubCommandPermissionCondition
extends PermissionCondition {
    private final Command.Condition aggregate;

    private SubCommandPermissionCondition(Set<String> perms, Command.Condition aggregate) {
        super(perms);
        this.aggregate = aggregate;
    }

    @Override
    public boolean satisfied(InjectedValueAccess context) {
        return this.aggregate.satisfied(context);
    }

    public static class Generator {
        private final List<Command> subCommands;

        public Generator(Collection<? extends Command> subCommands) {
            this.subCommands = ImmutableList.copyOf(subCommands);
        }

        public Command.Condition build() {
            List conditions = this.subCommands.stream().map(Command::getCondition).toList();
            List permConds = conditions.stream().map(c -> c.as(PermissionCondition.class)).toList();
            if (permConds.stream().anyMatch((Predicate<Optional>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, isEmpty(), (Ljava/util/Optional;)Z)())) {
                return new PermissionCondition((Set<String>)ImmutableSet.of());
            }
            Set<String> perms = permConds.stream().map(Optional::get).flatMap(cond -> cond.getPermissions().stream()).collect(Collectors.toSet());
            Command.Condition aggregate = permConds.stream().map(Optional::get).map(c -> c).reduce(Command.Condition::or).orElse(Command.Condition.TRUE);
            return new SubCommandPermissionCondition(perms, aggregate);
        }
    }
}

