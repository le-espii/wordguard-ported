/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.enginehub.piston.annotation.CommandCondition
 */
package com.sk89q.worldedit.command.util;

import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.enginehub.piston.annotation.CommandCondition;

@Retention(value=RetentionPolicy.RUNTIME)
@CommandCondition(value=CommandPermissionsConditionGenerator.class)
public @interface CommandPermissions {
    public String[] value();
}

