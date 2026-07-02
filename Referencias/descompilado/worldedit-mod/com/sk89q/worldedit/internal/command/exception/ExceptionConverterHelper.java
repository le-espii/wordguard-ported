/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.sk89q.worldedit.internal.command.exception;

import com.google.common.collect.ImmutableList;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.internal.command.exception.ExceptionConverter;
import com.sk89q.worldedit.internal.command.exception.ExceptionMatch;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.enginehub.piston.Command;
import org.enginehub.piston.exception.CommandExecutionException;

public abstract class ExceptionConverterHelper
implements ExceptionConverter {
    private final List<ExceptionHandler> handlers;

    public ExceptionConverterHelper() {
        ArrayList<ExceptionHandler> handlers = new ArrayList<ExceptionHandler>();
        for (Method method : this.getClass().getMethods()) {
            Class<?> cls;
            Class<?>[] parameters;
            if (method.getAnnotation(ExceptionMatch.class) == null || (parameters = method.getParameterTypes()).length != 1 || !Throwable.class.isAssignableFrom(cls = parameters[0])) continue;
            handlers.add(new ExceptionHandler(cls, method));
        }
        Collections.sort(handlers);
        this.handlers = handlers;
    }

    @Override
    public void convert(Throwable t) throws org.enginehub.piston.exception.CommandException {
        Class<?> throwableClass = t.getClass();
        for (ExceptionHandler handler : this.handlers) {
            if (!handler.cls.isAssignableFrom(throwableClass)) continue;
            try {
                handler.method.invoke(this, t);
            }
            catch (InvocationTargetException e) {
                if (e.getCause() instanceof org.enginehub.piston.exception.CommandException) {
                    throw (org.enginehub.piston.exception.CommandException)e.getCause();
                }
                if (e.getCause() instanceof CommandException) {
                    throw new CommandExecutionException(e.getCause(), (ImmutableList<Command>)ImmutableList.of());
                }
                throw new CommandExecutionException(e, (ImmutableList<Command>)ImmutableList.of());
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                throw new CommandExecutionException(e, (ImmutableList<Command>)ImmutableList.of());
            }
        }
    }

    private record ExceptionHandler(Class<? extends Throwable> cls, Method method) implements Comparable<ExceptionHandler>
    {
        @Override
        public int compareTo(ExceptionHandler o) {
            if (this.cls.equals(o.cls)) {
                return 0;
            }
            if (this.cls.isAssignableFrom(o.cls)) {
                return 1;
            }
            if (o.cls.isAssignableFrom(this.cls)) {
                return -1;
            }
            return this.cls.getCanonicalName().compareTo(o.cls.getCanonicalName());
        }
    }
}

