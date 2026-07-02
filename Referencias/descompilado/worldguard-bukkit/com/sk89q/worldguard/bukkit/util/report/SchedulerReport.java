/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.reflect.TypeToken
 *  com.sk89q.worldedit.util.report.DataReport
 *  javax.annotation.Nullable
 *  org.bukkit.Bukkit
 *  org.bukkit.scheduler.BukkitTask
 */
package com.sk89q.worldguard.bukkit.util.report;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.util.report.DataReport;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerReport
extends DataReport {
    private LoadingCache<Class<?>, Optional<Field>> taskFieldCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Optional<Field>>(this){

        public Optional<Field> load(Class<?> clazz) throws Exception {
            try {
                Field field = clazz.getDeclaredField("task");
                field.setAccessible(true);
                return Optional.ofNullable(field);
            }
            catch (NoSuchFieldException ignored) {
                return Optional.empty();
            }
        }
    });

    public SchedulerReport() {
        super("Scheduler");
        List tasks = Bukkit.getServer().getScheduler().getPendingTasks();
        this.append("Pending Task Count", tasks.size());
        for (BukkitTask task : tasks) {
            Class<?> taskClass = this.getTaskClass(task);
            DataReport report = new DataReport("Task: #" + task.getTaskId());
            report.append("Owner", task.getOwner().getName());
            report.append("Runnable", taskClass != null ? taskClass.getName() : "<Unknown>");
            report.append("Synchronous?", task.isSync());
            this.append(report.getTitle(), report);
        }
    }

    @Nullable
    private Class<?> getTaskClass(BukkitTask task) {
        try {
            Class clazz = task.getClass();
            Set classes = TypeToken.of((Class)clazz).getTypes().rawTypes();
            for (Class type : classes) {
                Optional field = (Optional)this.taskFieldCache.getUnchecked((Object)type);
                if (!field.isPresent()) continue;
                Object res = ((Field)field.get()).get(task);
                return res == null ? null : res.getClass();
            }
        }
        catch (IllegalAccessException | NoClassDefFoundError throwable) {
            // empty catch block
        }
        return null;
    }
}

