/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableTable
 *  com.google.common.collect.ImmutableTable$Builder
 *  com.google.common.collect.Table
 *  net.minecraft.core.BlockPosition
 *  net.minecraft.core.HolderLookup$a
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.resources.MinecraftKey
 *  net.minecraft.server.level.WorldServer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.GeneratorAccessSeed
 *  net.minecraft.world.level.block.entity.TileEntity
 *  net.minecraft.world.level.block.state.IBlockData
 *  net.minecraft.world.phys.Vec3D
 *  org.bukkit.World
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 *  org.jetbrains.annotations.Nullable
 */
package com.sk89q.worldedit.bukkit.adapter.impl.v1_21;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21.PaperweightAdapter;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_21.StaticRefraction;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.entity.EntityTypes;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.World;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.jetbrains.annotations.Nullable;

public class PaperweightServerLevelDelegateProxy
implements InvocationHandler {
    private final EditSession editSession;
    private final WorldServer serverLevel;
    private final PaperweightAdapter adapter;
    private static final Table<String, MethodType, MethodHandle> METHOD_MAP;

    private PaperweightServerLevelDelegateProxy(EditSession editSession, WorldServer serverLevel, PaperweightAdapter adapter) {
        this.editSession = editSession;
        this.serverLevel = serverLevel;
        this.adapter = adapter;
    }

    public static GeneratorAccessSeed newInstance(EditSession editSession, WorldServer serverLevel, PaperweightAdapter adapter) {
        return (GeneratorAccessSeed)Proxy.newProxyInstance(serverLevel.getClass().getClassLoader(), serverLevel.getClass().getInterfaces(), new PaperweightServerLevelDelegateProxy(editSession, serverLevel, adapter));
    }

    @Nullable
    private TileEntity getBlockEntity(BlockPosition blockPos) {
        TileEntity tileEntity = this.serverLevel.m(blockPos).c_(blockPos);
        if (tileEntity == null) {
            return null;
        }
        tileEntity.c((NBTTagCompound)this.adapter.fromNative(this.editSession.getFullBlock(BlockVector3.at(blockPos.u(), blockPos.v(), blockPos.w())).getNbtReference().getValue()), (HolderLookup.a)this.serverLevel.H_());
        return tileEntity;
    }

    private IBlockData getBlockState(BlockPosition blockPos) {
        return this.adapter.adapt(this.editSession.getBlock(BlockVector3.at(blockPos.u(), blockPos.v(), blockPos.w())));
    }

    private boolean setBlock(BlockPosition blockPos, IBlockData blockState) {
        try {
            return this.editSession.setBlock(BlockVector3.at(blockPos.u(), blockPos.v(), blockPos.w()), this.adapter.adapt(blockState));
        }
        catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean removeBlock(BlockPosition blockPos) {
        try {
            return this.editSession.setBlock(BlockVector3.at(blockPos.u(), blockPos.v(), blockPos.w()), BlockTypes.AIR.getDefaultState());
        }
        catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean addEntity(Entity entity) {
        Vec3D pos = entity.m(0.0f);
        Location location = new Location((Extent)BukkitAdapter.adapt((World)this.serverLevel.getWorld()), pos.a(), pos.b(), pos.c());
        MinecraftKey id = this.serverLevel.H_().d(Registries.z).b((Object)entity.am());
        NBTTagCompound tag = new NBTTagCompound();
        entity.f(tag);
        BaseEntity baseEntity = new BaseEntity(EntityTypes.get(id.toString()), LazyReference.from(() -> (LinCompoundTag)this.adapter.toNative((NBTBase)tag)));
        return this.editSession.createEntity(location, baseEntity) != null;
    }

    private static void addMethodHandleToTable(ImmutableTable.Builder<String, MethodType, MethodHandle> table, String methodName, MethodHandle methodHandle) {
        MethodHandle spreader = methodHandle.asSpreader(1, Object[].class, methodHandle.type().parameterCount() - 1);
        table.put((Object)methodName, (Object)methodHandle.type().dropParameterTypes(0, 1).changeReturnType(Void.TYPE), (Object)spreader);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHandle delegate = (MethodHandle)METHOD_MAP.get((Object)method.getName(), (Object)MethodType.methodType(Void.TYPE, method.getParameterTypes()));
        if (delegate != null) {
            return delegate.invoke(this, args);
        }
        return method.invoke(this.serverLevel, args);
    }

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        ImmutableTable.Builder builder = ImmutableTable.builder();
        try {
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.GET_BLOCK_STATE, lookup.unreflect(PaperweightServerLevelDelegateProxy.class.getDeclaredMethod("getBlockState", BlockPosition.class)));
            MethodHandle addEntity = lookup.unreflect(PaperweightServerLevelDelegateProxy.class.getDeclaredMethod("addEntity", Entity.class));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.ADD_FRESH_ENTITY_WITH_PASSENGERS_ENTITY, addEntity);
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.ADD_FRESH_ENTITY_WITH_PASSENGERS_ENTITY_SPAWN_REASON, MethodHandles.dropArguments(addEntity, 2, CreatureSpawnEvent.SpawnReason.class));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.ADD_FRESH_ENTITY, addEntity);
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.ADD_FRESH_ENTITY_SPAWN_REASON, MethodHandles.dropArguments(addEntity, 2, CreatureSpawnEvent.SpawnReason.class));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.GET_BLOCK_ENTITY, lookup.unreflect(PaperweightServerLevelDelegateProxy.class.getDeclaredMethod("getBlockEntity", BlockPosition.class)));
            MethodHandle setBlock = lookup.unreflect(PaperweightServerLevelDelegateProxy.class.getDeclaredMethod("setBlock", BlockPosition.class, IBlockData.class));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.SET_BLOCK, MethodHandles.dropArguments(setBlock, 3, Integer.TYPE));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.SET_BLOCK_MAX_UPDATE, MethodHandles.dropArguments(setBlock, 3, Integer.TYPE, Integer.TYPE));
            MethodHandle removeBlock = lookup.unreflect(PaperweightServerLevelDelegateProxy.class.getDeclaredMethod("removeBlock", BlockPosition.class));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.REMOVE_BLOCK, MethodHandles.dropArguments(removeBlock, 2, Boolean.TYPE));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.DESTROY_BLOCK, MethodHandles.dropArguments(removeBlock, 2, Boolean.TYPE));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.DESTROY_BLOCK_BREAKING_ENTITY, MethodHandles.dropArguments(removeBlock, 2, Boolean.TYPE, Entity.class));
            PaperweightServerLevelDelegateProxy.addMethodHandleToTable((ImmutableTable.Builder<String, MethodType, MethodHandle>)builder, StaticRefraction.DESTROY_BLOCK_BREAKING_ENTITY_MAX_UPDATE, MethodHandles.dropArguments(removeBlock, 2, Boolean.TYPE, Entity.class, Integer.TYPE));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to bind to own methods", e);
        }
        METHOD_MAP = builder.build();
    }
}

