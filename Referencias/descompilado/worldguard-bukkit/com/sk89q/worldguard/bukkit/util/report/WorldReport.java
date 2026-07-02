/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.util.report.DataReport
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.generator.ChunkGenerator
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.sk89q.worldedit.util.report.DataReport;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class WorldReport
extends DataReport {
    public WorldReport() {
        super("Worlds");
        List worlds = Bukkit.getServer().getWorlds();
        this.append("World Count", worlds.size());
        for (World world : worlds) {
            DataReport report = new DataReport("World: " + world.getName());
            report.append("UUID", (Object)world.getUID());
            report.append("World Type", (Object)world.getWorldType());
            report.append("Environment", (Object)world.getEnvironment());
            ChunkGenerator generator = world.getGenerator();
            report.append("Chunk Generator", generator != null ? generator.getClass().getName() : "<Default>");
            DataReport spawning = new DataReport("Spawning");
            spawning.append("Animals?", world.getAllowAnimals());
            spawning.append("Monsters?", world.getAllowMonsters());
            spawning.append("Ambient Spawn Limit", world.getAmbientSpawnLimit());
            spawning.append("Animal Spawn Limit", world.getAnimalSpawnLimit());
            spawning.append("Monster Spawn Limit", world.getMonsterSpawnLimit());
            spawning.append("Water Creature Spawn Limit", world.getWaterAnimalSpawnLimit());
            report.append(spawning.getTitle(), (Object)spawning);
            DataReport config = new DataReport("Configuration");
            config.append("Difficulty", (Object)world.getDifficulty());
            config.append("Max Height", world.getMaxHeight());
            config.append("Sea Level", world.getSeaLevel());
            report.append(config.getTitle(), (Object)config);
            DataReport state = new DataReport("State");
            state.append("Spawn Location", (Object)world.getSpawnLocation());
            state.append("Full Time", world.getFullTime());
            state.append("Weather Duration", world.getWeatherDuration());
            state.append("Thunder Duration", world.getThunderDuration());
            report.append(state.getTitle(), (Object)state);
            DataReport protection = new DataReport("Protection");
            protection.append("PVP?", world.getPVP());
            protection.append("Game Rules", Arrays.stream(world.getGameRules()).map(name -> name + "=" + world.getGameRuleValue(name)).collect(Collectors.joining(", ")));
            report.append(protection.getTitle(), (Object)protection);
            this.append(report.getTitle(), report);
        }
    }
}

