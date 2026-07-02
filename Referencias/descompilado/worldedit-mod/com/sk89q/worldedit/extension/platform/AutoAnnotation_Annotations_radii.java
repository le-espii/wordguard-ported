/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.extension.platform;

import com.sk89q.worldedit.internal.annotation.Radii;
import java.io.Serializable;

final class AutoAnnotation_Annotations_radii
implements Radii,
Serializable {
    private static final long serialVersionUID = -5526853588017907499L;
    private final int value;

    AutoAnnotation_Annotations_radii(int value) {
        this.value = value;
    }

    public Class<? extends Radii> annotationType() {
        return Radii.class;
    }

    @Override
    public int value() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("@com.sk89q.worldedit.internal.annotation.Radii(");
        sb.append(this.value);
        return sb.append(')').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Radii) {
            Radii that = (Radii)o;
            return this.value == that.value();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 0x4F9C270F ^ this.value;
    }
}

