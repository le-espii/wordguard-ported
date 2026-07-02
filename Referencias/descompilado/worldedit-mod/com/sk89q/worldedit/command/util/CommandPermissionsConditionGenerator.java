/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.sk89q.worldedit.command.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.PermissionCondition;
import java.lang.reflect.Method;
import java.util.Set;
import org.enginehub.piston.Command;
import org.enginehub.piston.gen.CommandConditionGenerator;
import org.enginehub.piston.util.NonnullByDefault;

@NonnullByDefault
public final class CommandPermissionsConditionGenerator
implements CommandConditionGenerator {
    @Override
    public Command.Condition generateCondition(Method commandMethod) {
        CommandPermissions annotation = commandMethod.getAnnotation(CommandPermissions.class);
        Preconditions.checkNotNull((Object)annotation, (Object)"Annotation is missing from commandMethod");
        ImmutableSet permissions = ImmutableSet.copyOf((Object[])annotation.value());
        return new PermissionCondition((Set<String>)permissions);
    }

    public static interface Registration {
        public Registration commandPermissionsConditionGenerator(CommandPermissionsConditionGenerator var1);
    }
}

