/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.enginehub.piston;

import javax.annotation.Nullable;
import org.enginehub.piston.CommandMetadata;
import org.enginehub.piston.CommandValue;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.part.ArgAcceptingCommandPart;
import org.enginehub.piston.part.CommandPart;

public interface CommandParameters
extends InjectedValueAccess {
    public boolean has(CommandPart var1);

    public CommandValue valueOf(ArgAcceptingCommandPart var1);

    @Nullable
    public CommandMetadata getMetadata();

    public ArgumentConverterAccess getConverters();
}

