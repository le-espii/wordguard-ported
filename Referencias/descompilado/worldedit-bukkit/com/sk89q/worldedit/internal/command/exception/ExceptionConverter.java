/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.command.exception;

import org.enginehub.piston.exception.CommandException;

public interface ExceptionConverter {
    public void convert(Throwable var1) throws CommandException;
}

