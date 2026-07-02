/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.minecraft.util.commands;

import com.sk89q.minecraft.util.commands.CommandUsageException;

@Deprecated
public class MissingNestedCommandException
extends CommandUsageException {
    public MissingNestedCommandException(String message, String usage) {
        super(message, usage);
    }
}

