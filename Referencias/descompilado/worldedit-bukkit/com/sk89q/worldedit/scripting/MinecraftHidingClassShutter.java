/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mozilla.javascript.ClassShutter
 */
package com.sk89q.worldedit.scripting;

import org.mozilla.javascript.ClassShutter;

class MinecraftHidingClassShutter
implements ClassShutter {
    MinecraftHidingClassShutter() {
    }

    public boolean visibleToScripts(String fullClassName) {
        return fullClassName.contains(".") || fullClassName.length() >= 4;
    }
}

