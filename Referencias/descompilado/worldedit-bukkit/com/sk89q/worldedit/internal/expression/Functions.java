/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSetMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.SetMultimap
 *  com.google.common.primitives.Doubles
 */
package com.sk89q.worldedit.internal.expression;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.primitives.Doubles;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectMap;
import com.sk89q.worldedit.bukkit.fastutil.ints.Int2ObjectOpenHashMap;
import com.sk89q.worldedit.internal.expression.EvaluationException;
import com.sk89q.worldedit.internal.expression.ExpressionEnvironment;
import com.sk89q.worldedit.internal.expression.ExpressionFunction;
import com.sk89q.worldedit.internal.expression.LocalSlot;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.noise.PerlinNoise;
import com.sk89q.worldedit.math.noise.RidgedMultiFractalNoise;
import com.sk89q.worldedit.math.noise.VoronoiNoise;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ThreadLocalRandom;

public final class Functions {
    private static final MethodHandle DOUBLE_VALUE;
    private static final Int2ObjectMap<double[]> globalMegaBuffer;
    private final Int2ObjectMap<double[]> megaBuffer = new Int2ObjectOpenHashMap<double[]>();
    private final SetMultimap<String, MethodHandle> map;
    private ExpressionEnvironment environment;
    private static final ThreadLocal<PerlinNoise> localPerlin;
    private static final ThreadLocal<VoronoiNoise> localVoronoi;
    private static final ThreadLocal<RidgedMultiFractalNoise> localRidgedMulti;

    static Functions create() {
        return new Functions();
    }

    private static MethodHandle clean(MethodHandle handle) {
        boolean wasVarargs = handle.isVarargsCollector();
        if ((handle = handle.asType(handle.type().wrap())).type().returnType() != Double.class) {
            Preconditions.checkState((boolean)Number.class.isAssignableFrom(handle.type().returnType()), (Object)"Function does not return a number");
            handle = handle.asType(handle.type().changeReturnType(Number.class));
            handle = MethodHandles.filterReturnValue(handle, DOUBLE_VALUE);
            handle = handle.asType(handle.type().wrap());
        }
        if (wasVarargs) {
            handle = handle.asVarargsCollector(handle.type().parameterType(handle.type().parameterCount() - 1));
        }
        return handle;
    }

    private static void addMathHandles(SetMultimap<String, MethodHandle> map, MethodHandles.Lookup lookup) throws NoSuchMethodException, IllegalAccessException {
        for (String name : ImmutableList.of((Object)"sin", (Object)"cos", (Object)"tan", (Object)"asin", (Object)"acos", (Object)"atan", (Object)"sinh", (Object)"cosh", (Object)"tanh", (Object)"sqrt", (Object)"cbrt", (Object)"abs", (Object[])new String[]{"ceil", "floor", "rint", "exp", "log", "log10"})) {
            map.put((Object)name, (Object)lookup.findStatic(Math.class, name, MethodType.methodType(Double.TYPE, Double.TYPE)));
        }
        map.put((Object)"ln", (Object)lookup.findStatic(Math.class, "log", MethodType.methodType(Double.TYPE, Double.TYPE)));
        map.put((Object)"round", (Object)lookup.findStatic(Math.class, "round", MethodType.methodType(Long.TYPE, Double.TYPE)));
        map.put((Object)"atan2", (Object)lookup.findStatic(Math.class, "atan2", MethodType.methodType(Double.TYPE, Double.TYPE, Double.TYPE)));
        map.put((Object)"min", (Object)lookup.findStatic(Doubles.class, "min", MethodType.methodType(Double.TYPE, double[].class)).asVarargsCollector(double[].class));
        map.put((Object)"max", (Object)lookup.findStatic(Doubles.class, "max", MethodType.methodType(Double.TYPE, double[].class)).asVarargsCollector(double[].class));
    }

    private void addFunctionHandles(SetMultimap<String, MethodHandle> map, MethodHandles.Lookup lookup) throws NoSuchMethodException, IllegalAccessException {
        for (Method method : Functions.class.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ExpressionFunction.class)) continue;
            if (Modifier.isStatic(method.getModifiers())) {
                map.put((Object)method.getName(), (Object)lookup.unreflect(method));
                continue;
            }
            map.put((Object)method.getName(), (Object)lookup.unreflectSpecial(method, Functions.class).bindTo(this));
        }
    }

    @ExpressionFunction
    private static double rotate(LocalSlot.Variable x, LocalSlot.Variable y, double angle) {
        double cosF = Math.cos(angle);
        double sinF = Math.sin(angle);
        double xOld = x.value();
        double yOld = y.value();
        x.setValue(xOld * cosF - yOld * sinF);
        y.setValue(xOld * sinF + yOld * cosF);
        return 0.0;
    }

    @ExpressionFunction
    private static double swap(LocalSlot.Variable x, LocalSlot.Variable y) {
        double tmp = x.value();
        x.setValue(y.value());
        y.setValue(tmp);
        return 0.0;
    }

    private Functions() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        HashMultimap map = HashMultimap.create();
        try {
            Functions.addMathHandles((SetMultimap<String, MethodHandle>)map, lookup);
            this.addFunctionHandles((SetMultimap<String, MethodHandle>)map, lookup);
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        this.map = ImmutableSetMultimap.copyOf((Multimap)Multimaps.transformValues((Multimap)map, Functions::clean));
    }

    public SetMultimap<String, MethodHandle> getMap() {
        return this.map;
    }

    public ExpressionEnvironment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(ExpressionEnvironment environment) {
        this.environment = environment;
    }

    private static double[] getSubBuffer(Int2ObjectMap<double[]> megabuf, int key) {
        return megabuf.computeIfAbsent(key, k -> new double[1024]);
    }

    private static double getBufferItem(Int2ObjectMap<double[]> megabuf, int index) {
        return Functions.getSubBuffer(megabuf, index & 0xFFFFFC00)[index & 0x3FF];
    }

    private static double setBufferItem(Int2ObjectMap<double[]> megabuf, int index, double value) {
        double d = value;
        Functions.getSubBuffer(megabuf, (int)(index & 0xFFFFFC00))[index & 0x3FF] = d;
        return d;
    }

    @ExpressionFunction
    private static double gmegabuf(double index) {
        return Functions.getBufferItem(globalMegaBuffer, (int)index);
    }

    @ExpressionFunction
    private static double gmegabuf(double index, double value) {
        return Functions.setBufferItem(globalMegaBuffer, (int)index, value);
    }

    @ExpressionFunction
    private double megabuf(double index) {
        return Functions.getBufferItem(this.megaBuffer, (int)index);
    }

    @ExpressionFunction
    private double megabuf(double index, double value) {
        return Functions.setBufferItem(this.megaBuffer, (int)index, value);
    }

    @ExpressionFunction
    private double closest(double x, double y, double z, double index, double count, double stride) {
        return Functions.findClosest(this.megaBuffer, x, y, z, (int)index, (int)count, (int)stride);
    }

    @ExpressionFunction
    private static double gclosest(double x, double y, double z, double index, double count, double stride) {
        return Functions.findClosest(globalMegaBuffer, x, y, z, (int)index, (int)count, (int)stride);
    }

    private static double findClosest(Int2ObjectMap<double[]> megabuf, double x, double y, double z, int index, int count, int stride) {
        int closestIndex = -1;
        double minDistanceSquared = Double.MAX_VALUE;
        for (int i = 0; i < count; ++i) {
            double currentZ;
            double currentY;
            double currentX = Functions.getBufferItem(megabuf, index) - x;
            double currentDistanceSquared = currentX * currentX + (currentY = Functions.getBufferItem(megabuf, index + 1) - y) * currentY + (currentZ = Functions.getBufferItem(megabuf, index + 2) - z) * currentZ;
            if (currentDistanceSquared < minDistanceSquared) {
                minDistanceSquared = currentDistanceSquared;
                closestIndex = index;
            }
            index += stride;
        }
        return closestIndex;
    }

    @ExpressionFunction
    private static double random() {
        return ThreadLocalRandom.current().nextDouble();
    }

    @ExpressionFunction
    private static double randint(double max) {
        return ThreadLocalRandom.current().nextInt((int)Math.floor(max));
    }

    @ExpressionFunction
    private static double perlin(double seed, double x, double y, double z, double frequency, double octaves, double persistence) {
        PerlinNoise perlin = localPerlin.get();
        try {
            perlin.setSeed((int)seed);
            perlin.setFrequency(frequency);
            perlin.setOctaveCount((int)octaves);
            perlin.setPersistence(persistence);
        }
        catch (IllegalArgumentException e) {
            throw new EvaluationException(0, "Perlin noise error: " + e.getMessage());
        }
        return perlin.noise(Vector3.at(x, y, z));
    }

    @ExpressionFunction
    private static double voronoi(double seed, double x, double y, double z, double frequency) {
        VoronoiNoise voronoi = localVoronoi.get();
        try {
            voronoi.setSeed((int)seed);
            voronoi.setFrequency(frequency);
        }
        catch (IllegalArgumentException e) {
            throw new EvaluationException(0, "Voronoi error: " + e.getMessage());
        }
        return voronoi.noise(Vector3.at(x, y, z));
    }

    @ExpressionFunction
    private static double ridgedmulti(double seed, double x, double y, double z, double frequency, double octaves) {
        RidgedMultiFractalNoise ridgedMulti = localRidgedMulti.get();
        try {
            ridgedMulti.setSeed((int)seed);
            ridgedMulti.setFrequency(frequency);
            ridgedMulti.setOctaveCount((int)octaves);
        }
        catch (IllegalArgumentException e) {
            throw new EvaluationException(0, "Ridged multi error: " + e.getMessage());
        }
        return ridgedMulti.noise(Vector3.at(x, y, z));
    }

    private static double queryInternal(LocalSlot type, LocalSlot data, double typeId, double dataValue) {
        double ret;
        double d = ret = !(type.value() != -1.0 && typeId != type.value() || data.value() != -1.0 && dataValue != data.value()) ? 1.0 : 0.0;
        if (type instanceof LocalSlot.Variable) {
            ((LocalSlot.Variable)type).setValue(typeId);
        }
        if (data instanceof LocalSlot.Variable) {
            ((LocalSlot.Variable)data).setValue(dataValue);
        }
        return ret;
    }

    @ExpressionFunction
    private double query(double x, double y, double z, LocalSlot type, LocalSlot data) {
        double typeId = this.environment.getBlockType(x, y, z);
        double dataValue = this.environment.getBlockData(x, y, z);
        return Functions.queryInternal(type, data, typeId, dataValue);
    }

    @ExpressionFunction
    private double queryAbs(double x, double y, double z, LocalSlot type, LocalSlot data) {
        double typeId = this.environment.getBlockTypeAbs(x, y, z);
        double dataValue = this.environment.getBlockDataAbs(x, y, z);
        return Functions.queryInternal(type, data, typeId, dataValue);
    }

    @ExpressionFunction
    private double queryRel(double x, double y, double z, LocalSlot type, LocalSlot data) {
        double typeId = this.environment.getBlockTypeRel(x, y, z);
        double dataValue = this.environment.getBlockDataRel(x, y, z);
        return Functions.queryInternal(type, data, typeId, dataValue);
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            DOUBLE_VALUE = lookup.findVirtual(Number.class, "doubleValue", MethodType.methodType(Double.TYPE));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        globalMegaBuffer = new Int2ObjectOpenHashMap<double[]>();
        localPerlin = ThreadLocal.withInitial(PerlinNoise::new);
        localVoronoi = ThreadLocal.withInitial(VoronoiNoise::new);
        localRidgedMulti = ThreadLocal.withInitial(RidgedMultiFractalNoise::new);
    }
}

