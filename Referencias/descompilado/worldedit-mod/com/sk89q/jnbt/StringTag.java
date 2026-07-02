/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.jnbt;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.Tag;
import org.enginehub.linbus.tree.LinStringTag;

@Deprecated
public final class StringTag
extends Tag<String, LinStringTag> {
    public StringTag(String value) {
        super(LinStringTag.of((String)Preconditions.checkNotNull((Object)value)));
    }

    public StringTag(LinStringTag tag) {
        super(tag);
    }

    @Override
    public String getValue() {
        return (String)super.getValue();
    }
}

