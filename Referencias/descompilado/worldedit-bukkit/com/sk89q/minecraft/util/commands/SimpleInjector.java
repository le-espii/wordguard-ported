/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package com.sk89q.minecraft.util.commands;

import com.sk89q.minecraft.util.commands.Injector;
import com.sk89q.worldedit.internal.util.LogManagerCompat;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.Logger;

@Deprecated
public class SimpleInjector
implements Injector {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private final Object[] args;
    private final Class<?>[] argClasses;

    public SimpleInjector(Object ... args) {
        this.args = args;
        this.argClasses = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            this.argClasses[i] = args[i].getClass();
        }
    }

    @Override
    public Object getInstance(Class<?> clazz) {
        try {
            Constructor<?> ctr = clazz.getConstructor(this.argClasses);
            ctr.setAccessible(true);
            return ctr.newInstance(this.args);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            LOGGER.error("Error initializing commands class " + String.valueOf(clazz), (Throwable)e);
            return null;
        }
    }
}

