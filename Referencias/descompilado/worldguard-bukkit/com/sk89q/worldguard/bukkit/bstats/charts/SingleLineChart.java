/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.bukkit.bstats.charts;

import com.sk89q.worldguard.bukkit.bstats.charts.CustomChart;
import com.sk89q.worldguard.bukkit.bstats.json.JsonObjectBuilder;
import java.util.concurrent.Callable;

public class SingleLineChart
extends CustomChart {
    private final Callable<Integer> callable;

    public SingleLineChart(String chartId, Callable<Integer> callable) {
        super(chartId);
        this.callable = callable;
    }

    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        int value = this.callable.call();
        if (value == 0) {
            return null;
        }
        return new JsonObjectBuilder().appendField("value", value).build();
    }
}

