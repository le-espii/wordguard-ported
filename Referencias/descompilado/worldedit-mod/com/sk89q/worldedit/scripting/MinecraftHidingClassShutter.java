/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.scripting;

import org.mozilla.javascript.ClassShutter;

class MinecraftHidingClassShutter
implements ClassShutter {
    MinecraftHidingClassShutter() {
    }

    @Override
    public boolean visibleToScripts(String fullClassName) {
        return fullClassName.contains(".") || fullClassName.length() >= 4;
    }
}

