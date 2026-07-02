/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.gen;

import java.lang.reflect.Method;
import org.enginehub.piston.Command;

public interface CommandConditionGenerator {
    public Command.Condition generateCondition(Method var1);
}

