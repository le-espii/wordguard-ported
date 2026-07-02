/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.flags;

import com.sk89q.worldguard.protection.flags.StateFlag;

class BuildFlag
extends StateFlag {
    public BuildFlag(String name, boolean def) {
        super(name, def);
    }

    @Override
    public boolean implicitlySetWithMembership() {
        return true;
    }

    @Override
    public boolean usesMembershipAsDefault() {
        return true;
    }

    @Override
    public boolean preventsAllowOnGlobal() {
        return true;
    }

    @Override
    public boolean requiresSubject() {
        return true;
    }
}

