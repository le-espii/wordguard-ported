/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.entity;

import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.util.Faceted;
import javax.annotation.Nullable;

public interface Entity
extends Faceted,
Locatable {
    @Nullable
    public BaseEntity getState();

    public boolean remove();
}

