/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.regions;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.internal.util.DeprecationUtil;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.selector.limit.SelectorLimits;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public interface RegionSelector {
    @Nullable
    public World getWorld();

    public void setWorld(@Nullable World var1);

    public boolean selectPrimary(BlockVector3 var1, SelectorLimits var2);

    public boolean selectSecondary(BlockVector3 var1, SelectorLimits var2);

    public void explainPrimarySelection(Actor var1, LocalSession var2, BlockVector3 var3);

    public void explainSecondarySelection(Actor var1, LocalSession var2, BlockVector3 var3);

    public void explainRegionAdjust(Actor var1, LocalSession var2);

    public BlockVector3 getPrimaryPosition() throws IncompleteRegionException;

    public Region getRegion() throws IncompleteRegionException;

    public Region getIncompleteRegion();

    public boolean isDefined();

    @Deprecated
    default public int getArea() {
        return (int)this.getVolume();
    }

    @NonAbstractForCompatibility(delegateName="getArea", delegateParams={})
    default public long getVolume() {
        DeprecationUtil.checkDelegatingOverride(this.getClass());
        return this.getArea();
    }

    public void learnChanges();

    public void clear();

    public String getTypeName();

    @Deprecated
    default public List<String> getInformationLines() {
        return Lists.newArrayList();
    }

    default public List<Component> getSelectionInfoLines() {
        return this.getInformationLines().stream().map(line -> TextComponent.of(line, TextColor.LIGHT_PURPLE)).collect(Collectors.toList());
    }
}

