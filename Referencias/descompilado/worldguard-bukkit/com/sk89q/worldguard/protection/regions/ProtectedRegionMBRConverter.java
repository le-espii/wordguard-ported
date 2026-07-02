/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.protection.regions;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.khelekore.prtree.MBRConverter;

public class ProtectedRegionMBRConverter
implements MBRConverter<ProtectedRegion> {
    @Override
    public int getDimensions() {
        return 3;
    }

    @Override
    public double getMax(int dimension, ProtectedRegion region) {
        return switch (dimension) {
            case 0 -> region.getMaximumPoint().x();
            case 1 -> region.getMaximumPoint().y();
            case 2 -> region.getMaximumPoint().z();
            default -> 0.0;
        };
    }

    @Override
    public double getMin(int dimension, ProtectedRegion region) {
        return switch (dimension) {
            case 0 -> region.getMinimumPoint().x();
            case 1 -> region.getMinimumPoint().y();
            case 2 -> region.getMinimumPoint().z();
            default -> 0.0;
        };
    }
}

