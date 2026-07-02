/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.worldedit.command.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.sk89q.worldedit.command.util.FutureProgressListener;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.command.exception.ExceptionConverter;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import com.sk89q.worldedit.util.formatting.component.ErrorFormat;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.task.FutureForwardingTask;
import com.sk89q.worldedit.util.task.Supervisor;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import org.enginehub.piston.Command;
import org.enginehub.piston.exception.CommandException;
import org.enginehub.piston.exception.CommandExecutionException;

public final class AsyncCommandBuilder<T> {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final Callable<T> callable;
    private final Actor sender;
    @Nullable
    private Supervisor supervisor;
    @Nullable
    private String description;
    @Nullable
    private Component delayMessage;
    @Nullable
    private Component workingMessage;
    @Nullable
    private Component successMessage;
    @Nullable
    private Consumer<T> consumer;
    @Nullable
    private Component failureMessage;
    @Nullable
    private ExceptionConverter exceptionConverter;

    private AsyncCommandBuilder(Callable<T> callable, Actor sender) {
        Preconditions.checkNotNull(callable);
        Preconditions.checkNotNull((Object)sender);
        this.callable = callable;
        this.sender = sender;
    }

    public static <T> AsyncCommandBuilder<T> wrap(Callable<T> callable, Actor sender) {
        return new AsyncCommandBuilder<T>(callable, sender);
    }

    public AsyncCommandBuilder<T> registerWithSupervisor(Supervisor supervisor, String description) {
        this.supervisor = (Supervisor)Preconditions.checkNotNull((Object)supervisor);
        this.description = (String)Preconditions.checkNotNull((Object)description);
        return this;
    }

    @Deprecated
    public AsyncCommandBuilder<T> sendMessageAfterDelay(String message) {
        return this.sendMessageAfterDelay(TextComponent.of((String)Preconditions.checkNotNull((Object)message)));
    }

    @Deprecated
    public AsyncCommandBuilder<T> sendMessageAfterDelay(Component message) {
        return this.setDelayMessage(message);
    }

    public AsyncCommandBuilder<T> setDelayMessage(Component message) {
        this.delayMessage = (Component)Preconditions.checkNotNull((Object)message);
        return this;
    }

    public AsyncCommandBuilder<T> setWorkingMessage(Component message) {
        Preconditions.checkNotNull((Object)this.delayMessage, (Object)"Must have a delay message if using a working message");
        this.workingMessage = (Component)Preconditions.checkNotNull((Object)message);
        return this;
    }

    public AsyncCommandBuilder<T> onSuccess(@Nullable Component message, @Nullable Consumer<T> consumer) {
        Preconditions.checkArgument((message != null || consumer != null ? 1 : 0) != 0, (Object)"Can't have null message AND consumer");
        this.successMessage = message;
        this.consumer = consumer;
        return this;
    }

    public AsyncCommandBuilder<T> onSuccess(@Nullable String message, @Nullable Consumer<T> consumer) {
        Preconditions.checkArgument((message != null || consumer != null ? 1 : 0) != 0, (Object)"Can't have null message AND consumer");
        this.successMessage = message == null ? null : TextComponent.of(message, TextColor.LIGHT_PURPLE);
        this.consumer = consumer;
        return this;
    }

    public AsyncCommandBuilder<T> onFailure(@Nullable Component message, @Nullable ExceptionConverter exceptionConverter) {
        Preconditions.checkArgument((message != null || exceptionConverter != null ? 1 : 0) != 0, (Object)"Can't have null message AND exceptionConverter");
        this.failureMessage = message;
        this.exceptionConverter = exceptionConverter;
        return this;
    }

    public AsyncCommandBuilder<T> onFailure(@Nullable String message, @Nullable ExceptionConverter exceptionConverter) {
        Preconditions.checkArgument((message != null || exceptionConverter != null ? 1 : 0) != 0, (Object)"Can't have null message AND exceptionConverter");
        this.failureMessage = message == null ? null : ErrorFormat.wrap(message);
        this.exceptionConverter = exceptionConverter;
        return this;
    }

    public ListenableFuture<T> buildAndExec(ListeningExecutorService executor) {
        ListenableFuture future = ((ListeningExecutorService)Preconditions.checkNotNull((Object)executor)).submit(this::runTask);
        if (this.delayMessage != null) {
            FutureProgressListener.addProgressListener(future, this.sender, this.delayMessage, this.workingMessage);
        }
        if (this.supervisor != null && this.description != null) {
            this.supervisor.monitor(FutureForwardingTask.create(future, this.description, this.sender));
        }
        return future;
    }

    private T runTask() {
        T result;
        block11: {
            result = null;
            try {
                result = this.callable.call();
                if (this.consumer != null) {
                    this.consumer.accept(result);
                }
                if (this.successMessage != null) {
                    this.sender.print(this.successMessage);
                }
            }
            catch (Throwable orig) {
                Component failure = this.failureMessage != null ? this.failureMessage : TextComponent.of("An error occurred");
                try {
                    if (this.exceptionConverter != null) {
                        try {
                            if (orig instanceof com.sk89q.minecraft.util.commands.CommandException) {
                                throw new CommandExecutionException(orig, (ImmutableList<Command>)ImmutableList.of());
                            }
                            this.exceptionConverter.convert(orig);
                            throw orig;
                        }
                        catch (CommandException converted) {
                            Component message = this.tryExtractOldCommandException(converted);
                            if (message == null) {
                                message = Strings.isNullOrEmpty((String)converted.getMessage()) ? TextComponent.of("Unknown error.") : converted.getRichMessage();
                            }
                            this.sender.printError(failure.append(TextComponent.of(": ")).append(message));
                            break block11;
                        }
                    }
                    throw orig;
                }
                catch (Throwable unknown) {
                    this.sender.printError(failure.append(TextComponent.of(": Unknown error. Please see console.")));
                    LOGGER.error("Uncaught exception occurred in task: " + this.description, orig);
                }
            }
        }
        return result;
    }

    private Component tryExtractOldCommandException(CommandException converted) {
        TextComponent message = null;
        if (converted instanceof CommandExecutionException) {
            Throwable parentCause = converted;
            while ((parentCause = parentCause.getCause()) != null) {
                if (!(parentCause instanceof com.sk89q.minecraft.util.commands.CommandException)) continue;
                String msg = parentCause.getMessage();
                if (Strings.isNullOrEmpty((String)msg)) break;
                message = TextComponent.of(msg);
                break;
            }
        }
        return message;
    }
}

