/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.internal.command.exception;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.InvalidItemException;
import com.sk89q.worldedit.MaxBrushRadiusException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.MaxRadiusException;
import com.sk89q.worldedit.MissingWorldException;
import com.sk89q.worldedit.UnknownDirectionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.InsufficientArgumentsException;
import com.sk89q.worldedit.command.tool.InvalidToolBindException;
import com.sk89q.worldedit.extension.input.DisallowedUsageException;
import com.sk89q.worldedit.extension.input.NoMatchException;
import com.sk89q.worldedit.extent.clipboard.io.SchematicLoadException;
import com.sk89q.worldedit.internal.command.exception.ExceptionConverterHelper;
import com.sk89q.worldedit.internal.command.exception.ExceptionMatch;
import com.sk89q.worldedit.internal.expression.ExpressionException;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.io.file.FileSelectionAbortedException;
import com.sk89q.worldedit.util.io.file.FilenameResolutionException;
import com.sk89q.worldedit.util.io.file.InvalidFilenameException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.enginehub.piston.Command;
import org.enginehub.piston.exception.CommandException;
import org.enginehub.piston.exception.UsageException;

public class WorldEditExceptionConverter
extends ExceptionConverterHelper {
    private static final Pattern numberFormat = Pattern.compile("^For input string: \"(.*)\"$");
    private final WorldEdit worldEdit;

    public WorldEditExceptionConverter(WorldEdit worldEdit) {
        Preconditions.checkNotNull((Object)worldEdit);
        this.worldEdit = worldEdit;
    }

    private CommandException newCommandException(String message, Throwable cause) {
        return this.newCommandException(TextComponent.of(String.valueOf(message)), cause);
    }

    private CommandException newCommandException(Component message, Throwable cause) {
        return new CommandException(message, cause, (ImmutableList<Command>)ImmutableList.of());
    }

    @ExceptionMatch
    public void convert(NumberFormatException e) throws CommandException {
        Matcher matcher = numberFormat.matcher(e.getMessage());
        if (matcher.matches()) {
            throw this.newCommandException(TranslatableComponent.of("worldedit.error.invalid-number.matches", TextComponent.of(matcher.group(1))), (Throwable)e);
        }
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.invalid-number"), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(IncompleteRegionException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.incomplete-region"), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(MissingWorldException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(NoMatchException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @Deprecated
    @ExceptionMatch
    public void convert(InvalidItemException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(DisallowedUsageException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(MaxChangedBlocksException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.max-changes", TextComponent.of(e.getBlockLimit())), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(MaxBrushRadiusException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.max-brush-radius", TextComponent.of(this.worldEdit.getConfiguration().maxBrushRadius)), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(MaxRadiusException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.max-radius", TextComponent.of(this.worldEdit.getConfiguration().maxRadius)), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(UnknownDirectionException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(InsufficientArgumentsException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(RegionOperationException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(ExpressionException e) throws CommandException {
        throw this.newCommandException(e.getMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(EmptyClipboardException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.empty-clipboard"), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(InvalidFilenameException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.invalid-filename", TextComponent.of(e.getFilename()), e.getRichMessage()), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(FilenameResolutionException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.file-resolution", TextComponent.of(e.getFilename()), e.getRichMessage()), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(InvalidToolBindException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.tool.error.cannot-bind", e.getItemType().getRichName(), e.getRichMessage()), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(FileSelectionAbortedException e) throws CommandException {
        throw this.newCommandException(TranslatableComponent.of("worldedit.error.file-aborted"), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(SchematicLoadException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(WorldEditException e) throws CommandException {
        throw this.newCommandException(e.getRichMessage(), (Throwable)e);
    }

    @ExceptionMatch
    public void convert(UsageException e) throws CommandException {
        throw e;
    }
}

