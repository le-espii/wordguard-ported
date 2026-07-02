/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import java.util.Collection;
import javax.annotation.Nullable;

public class StateFlag
extends Flag<State> {
    private boolean def;

    public StateFlag(String name, boolean def, RegionGroup defaultGroup) {
        super(name, defaultGroup);
        this.def = def;
    }

    public StateFlag(String name, boolean def) {
        super(name);
        this.def = def;
    }

    @Override
    public State getDefault() {
        return this.def ? State.ALLOW : null;
    }

    @Override
    public boolean hasConflictStrategy() {
        return true;
    }

    @Override
    @Nullable
    public State chooseValue(Collection<State> values) {
        if (!values.isEmpty()) {
            return StateFlag.combine(values);
        }
        return null;
    }

    public boolean preventsAllowOnGlobal() {
        return false;
    }

    @Override
    public State parseInput(FlagContext context) throws InvalidFlagFormat {
        String input = context.getUserInput();
        if (input.equalsIgnoreCase("allow")) {
            return State.ALLOW;
        }
        if (input.equalsIgnoreCase("deny")) {
            return State.DENY;
        }
        if (input.equalsIgnoreCase("none")) {
            return null;
        }
        throw new InvalidFlagFormat("Expected none/allow/deny but got '" + input + "'");
    }

    @Override
    public State unmarshal(Object o) {
        String str = o.toString();
        if (str.equalsIgnoreCase("allow")) {
            return State.ALLOW;
        }
        if (str.equalsIgnoreCase("deny")) {
            return State.DENY;
        }
        return null;
    }

    @Override
    public Object marshal(State o) {
        if (o == State.ALLOW) {
            return "allow";
        }
        if (o == State.DENY) {
            return "deny";
        }
        return null;
    }

    public static boolean test(State ... states) {
        boolean allowed = false;
        for (State state : states) {
            if (state == State.DENY) {
                return false;
            }
            if (state != State.ALLOW) continue;
            allowed = true;
        }
        return allowed;
    }

    @Nullable
    public static State combine(State ... states) {
        boolean allowed = false;
        for (State state : states) {
            if (state == State.DENY) {
                return State.DENY;
            }
            if (state != State.ALLOW) continue;
            allowed = true;
        }
        return allowed ? State.ALLOW : null;
    }

    @Nullable
    public static State combine(Collection<State> states) {
        boolean allowed = false;
        for (State state : states) {
            if (state == State.DENY) {
                return State.DENY;
            }
            if (state != State.ALLOW) continue;
            allowed = true;
        }
        return allowed ? State.ALLOW : null;
    }

    @Nullable
    public static State allowOrNone(boolean flag) {
        return flag ? State.ALLOW : null;
    }

    @Nullable
    public static State denyToNone(State state) {
        return state == State.DENY ? null : state;
    }

    public static enum State {
        ALLOW,
        DENY;

    }
}

