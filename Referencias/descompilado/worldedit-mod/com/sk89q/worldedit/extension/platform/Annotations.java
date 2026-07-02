/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoAnnotation
 */
package com.sk89q.worldedit.extension.platform;

import com.google.auto.value.AutoAnnotation;
import com.sk89q.worldedit.extension.platform.AutoAnnotation_Annotations_radii;
import com.sk89q.worldedit.internal.annotation.Radii;

class Annotations {
    @AutoAnnotation
    static Radii radii(int value) {
        return new AutoAnnotation_Annotations_radii(value);
    }

    private Annotations() {
    }
}

