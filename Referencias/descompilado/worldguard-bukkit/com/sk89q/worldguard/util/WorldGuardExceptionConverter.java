/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.worldedit.WorldEditException
 *  com.sk89q.worldedit.internal.command.exception.ExceptionConverterHelper
 *  com.sk89q.worldedit.internal.command.exception.ExceptionMatch
 *  com.sk89q.worldedit.util.auth.AuthorizationException
 *  com.sk89q.worldedit.util.formatting.component.InvalidComponentException
 */
package com.sk89q.worldguard.util;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.internal.command.exception.ExceptionConverterHelper;
import com.sk89q.worldedit.internal.command.exception.ExceptionMatch;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.component.InvalidComponentException;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.util.UnresolvedNamesException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldGuardExceptionConverter
extends ExceptionConverterHelper {
    private static final Pattern numberFormat = Pattern.compile("^For input string: \"(.*)\"$");

    private CommandException newCommandException(String message, Throwable cause) {
        return new CommandException(message, cause);
    }

    @ExceptionMatch
    public void convert(NumberFormatException e) throws CommandException {
        Matcher matcher = numberFormat.matcher(e.getMessage());
        if (matcher.matches()) {
            throw this.newCommandException("Number expected; string \"" + matcher.group(1) + "\" given.", e);
        }
        throw this.newCommandException("Number expected; string given.", e);
    }

    @ExceptionMatch
    public void convert(InvalidComponentException e) throws CommandException {
        throw this.newCommandException(e.getMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(StorageException e) throws CommandException {
        WorldGuard.logger.log(Level.WARNING, "Error loading/saving regions", e);
        throw this.newCommandException("Region data could not be loaded/saved: " + e.getMessage(), e);
    }

    @ExceptionMatch
    public void convert(RejectedExecutionException e) throws CommandException {
        throw this.newCommandException("There are currently too many tasks queued to add yours. Use /wg running to list queued and running tasks.", e);
    }

    @ExceptionMatch
    public void convert(CancellationException e) throws CommandException {
        throw this.newCommandException("Task was cancelled.", e);
    }

    @ExceptionMatch
    public void convert(InterruptedException e) throws CommandException {
        throw this.newCommandException("Task was interrupted.", e);
    }

    @ExceptionMatch
    public void convert(WorldEditException e) throws CommandException {
        throw this.newCommandException(e.getMessage(), e);
    }

    @ExceptionMatch
    public void convert(UnresolvedNamesException e) throws CommandException {
        throw this.newCommandException(e.getMessage(), e);
    }

    @ExceptionMatch
    public void convert(AuthorizationException e) throws CommandException {
        throw this.newCommandException("You don't have permission to do that.", (Throwable)e);
    }
}

