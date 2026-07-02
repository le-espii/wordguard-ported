/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.protection.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class NormativeOrders {
    private static final PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator();

    private NormativeOrders() {
    }

    public static void sort(List<ProtectedRegion> regions) {
        NormativeOrders.sortInto(Sets.newHashSet(regions), regions);
    }

    public static List<ProtectedRegion> fromSet(Set<ProtectedRegion> regions) {
        List<ProtectedRegion> sorted = Arrays.asList(new ProtectedRegion[regions.size()]);
        NormativeOrders.sortInto(regions, sorted);
        return sorted;
    }

    private static void sortInto(Set<ProtectedRegion> regions, List<ProtectedRegion> sorted) {
        ArrayList root = Lists.newArrayList();
        HashMap nodes = Maps.newHashMap();
        for (ProtectedRegion region : regions) {
            NormativeOrders.addNode(nodes, root, region);
        }
        int index = regions.size() - 1;
        for (RegionNode node : root) {
            while (node != null) {
                if (regions.contains(node.region)) {
                    sorted.set(index, node.region);
                    --index;
                }
                node = node.next;
            }
        }
        Collections.sort(sorted, PRIORITY_COMPARATOR);
    }

    private static RegionNode addNode(Map<ProtectedRegion, RegionNode> nodes, List<RegionNode> root, ProtectedRegion region) {
        RegionNode node = nodes.get(region);
        if (node == null) {
            node = new RegionNode(region);
            nodes.put(region, node);
            if (region.getParent() != null) {
                NormativeOrders.addNode(nodes, root, region.getParent()).insertAfter(node);
            } else {
                root.add(node);
            }
        }
        return node;
    }

    private static class RegionNode {
        @Nullable
        private RegionNode next;
        private final ProtectedRegion region;

        private RegionNode(ProtectedRegion region) {
            this.region = region;
        }

        private void insertAfter(RegionNode node) {
            if (this.next == null) {
                this.next = node;
            } else {
                node.next = this.next;
                this.next = node;
            }
        }
    }

    private static class PriorityComparator
    implements Comparator<ProtectedRegion> {
        private PriorityComparator() {
        }

        @Override
        public int compare(ProtectedRegion o1, ProtectedRegion o2) {
            if (o1.getPriority() > o2.getPriority()) {
                return -1;
            }
            if (o1.getPriority() < o2.getPriority()) {
                return 1;
            }
            return 0;
        }
    }
}

