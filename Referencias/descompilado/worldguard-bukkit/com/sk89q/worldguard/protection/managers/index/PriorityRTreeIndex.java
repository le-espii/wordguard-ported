/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.math.BlockVector3
 */
package com.sk89q.worldguard.protection.managers.index;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.index.HashMapIndex;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegionMBRConverter;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import org.khelekore.prtree.MBRConverter;
import org.khelekore.prtree.PRTree;
import org.khelekore.prtree.SimpleMBR;

public class PriorityRTreeIndex
extends HashMapIndex {
    private static final int BRANCH_FACTOR = 30;
    private static final MBRConverter<ProtectedRegion> CONVERTER = new ProtectedRegionMBRConverter();
    private PRTree<ProtectedRegion> tree = new PRTree<ProtectedRegion>(CONVERTER, 30);

    public PriorityRTreeIndex() {
        this.tree.load(Collections.emptyList());
    }

    @Override
    protected void rebuildIndex() {
        PRTree<ProtectedRegion> newTree = new PRTree<ProtectedRegion>(CONVERTER, 30);
        newTree.load(this.values());
        this.tree = newTree;
    }

    @Override
    public void applyContaining(BlockVector3 position, Predicate<ProtectedRegion> consumer) {
        HashSet<ProtectedRegion> seen = new HashSet<ProtectedRegion>();
        SimpleMBR pointMBR = new SimpleMBR(position.x(), position.x(), position.y(), position.y(), position.z(), position.z());
        for (ProtectedRegion region : this.tree.find(pointMBR)) {
            if (!region.contains(position) || seen.contains(region)) continue;
            seen.add(region);
            if (consumer.test(region)) continue;
            break;
        }
    }

    @Override
    public void applyIntersecting(ProtectedRegion region, Predicate<ProtectedRegion> consumer) {
        BlockVector3 min = region.getMinimumPoint().floor();
        BlockVector3 max = region.getMaximumPoint().ceil();
        HashSet<ProtectedRegion> candidates = new HashSet<ProtectedRegion>();
        SimpleMBR pointMBR = new SimpleMBR(min.x(), max.x(), min.y(), max.y(), min.z(), max.z());
        for (ProtectedRegion found : this.tree.find(pointMBR)) {
            candidates.add(found);
        }
        for (ProtectedRegion found : region.getIntersectingRegions(candidates)) {
            if (!consumer.test(found)) break;
        }
    }

    public static final class Factory
    implements Function<String, PriorityRTreeIndex> {
        @Override
        public PriorityRTreeIndex apply(String name) {
            return new PriorityRTreeIndex();
        }
    }
}

