/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.eventbus;

import com.sk89q.worldedit.util.eventbus.EventHandler;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Objects;

public class MethodHandleEventHandler
extends EventHandler {
    private final MethodHandle methodHandle;
    private final String methodName;
    private final Object object;

    protected MethodHandleEventHandler(EventHandler.Priority priority, Object object, MethodHandle methodHandle, String methodName) {
        super(priority);
        this.object = object;
        this.methodHandle = methodHandle.bindTo(object).asType(MethodType.methodType(Void.TYPE, Object.class));
        this.methodName = methodName;
    }

    @Override
    public void dispatch(Object event) throws Exception {
        try {
            this.methodHandle.invokeExact(event);
        }
        catch (Error | Exception e) {
            throw e;
        }
        catch (Throwable t) {
            throw new Exception(t);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.methodName, this.object);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MethodHandleEventHandler that = (MethodHandleEventHandler)o;
        if (!this.methodName.equals(that.methodName)) {
            return false;
        }
        return Objects.equals(this.object, that.object);
    }
}

