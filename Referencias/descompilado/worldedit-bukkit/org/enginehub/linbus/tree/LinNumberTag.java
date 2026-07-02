/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.tree;

import org.enginehub.linbus.tree.LinByteTag;
import org.enginehub.linbus.tree.LinDoubleTag;
import org.enginehub.linbus.tree.LinFloatTag;
import org.enginehub.linbus.tree.LinIntTag;
import org.enginehub.linbus.tree.LinLongTag;
import org.enginehub.linbus.tree.LinShortTag;
import org.enginehub.linbus.tree.LinTag;

public abstract sealed class LinNumberTag<T extends Number>
extends LinTag<T>
permits LinByteTag, LinDoubleTag, LinFloatTag, LinIntTag, LinLongTag, LinShortTag {
    protected LinNumberTag() {
    }
}

