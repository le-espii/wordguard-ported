/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 */
package com.sk89q.worldguard.protection.flags;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sk89q.worldguard.protection.flags.Flag;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FlagUtil {
    private static final Logger log = Logger.getLogger(FlagUtil.class.getCanonicalName());

    private FlagUtil() {
    }

    public static Map<String, Object> marshal(Map<Flag<?>, Object> values) {
        Preconditions.checkNotNull(values, (Object)"values");
        HashMap rawValues = Maps.newHashMap();
        for (Map.Entry<Flag<?>, Object> entry : values.entrySet()) {
            try {
                rawValues.put(entry.getKey().getName(), FlagUtil.marshal(entry.getKey(), entry.getValue()));
            }
            catch (Throwable e) {
                log.log(Level.WARNING, "Failed to marshal flag value for " + String.valueOf(entry.getKey()) + "; value is " + String.valueOf(entry.getValue()), e);
            }
        }
        return rawValues;
    }

    private static <T> Object marshal(Flag<T> flag, Object value) {
        return flag.marshal(value);
    }
}

