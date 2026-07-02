/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  org.jetbrains.annotations.Nullable
 */
package com.sk89q.worldedit.bukkit.adapter.impl.v1_20_R4;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.adapter.impl.v1_20_R4.PaperweightAdapter;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.concurrency.LazyReference;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.entity.EntityTypes;
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
import org.enginehub.linbus.tree.LinCompoundTag;
import org.jetbrains.annotations.Nullable;

public class PaperweightServerLevelDelegateProxy
implements InvocationHandler {
    private final EditSession editSession;
    private final WorldServer serverLevel;
    private final PaperweightAdapter adapter;

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

    private boolean removeBlock(BlockPosition blockPos, boolean bl) {
        try {
            return this.editSession.setBlock(BlockVector3.at(blockPos.u(), blockPos.v(), blockPos.w()), BlockTypes.AIR.getDefaultState());
        }
        catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean addEntity(Entity entity) {
        Vec3D pos = entity.l(0.0f);
        Location location = new Location((Extent)BukkitAdapter.adapt((World)this.serverLevel.getWorld()), pos.a(), pos.b(), pos.c());
        MinecraftKey id = this.serverLevel.H_().d(Registries.v).b((Object)entity.ak());
        NBTTagCompound tag = new NBTTagCompound();
        entity.f(tag);
        BaseEntity baseEntity = new BaseEntity(EntityTypes.get(id.toString()), LazyReference.from(() -> (LinCompoundTag)this.adapter.toNative((NBTBase)tag)));
        return this.editSession.createEntity(location, baseEntity) != null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "a_": 
            case "getBlockState": 
            case "addFreshEntityWithPassengers": {
                Object object;
                if (args.length == 1 && (object = args[0]) instanceof BlockPosition) {
                    BlockPosition blockPos = (BlockPosition)object;
                    return this.getBlockState(blockPos);
                }
                if (args.length < 1 || !((object = args[0]) instanceof Entity)) break;
                Entity entity = (Entity)object;
                return this.addEntity(entity);
            }
            case "c_": 
            case "getBlockEntity": {
                Object entity;
                if (args.length != 1 || !((entity = args[0]) instanceof BlockPosition)) break;
                BlockPosition blockPos = (BlockPosition)entity;
                return this.getBlockEntity(blockPos);
            }
            case "a": 
            case "setBlock": 
            case "removeBlock": 
            case "destroyBlock": {
                Object object;
                if (args.length >= 2 && (object = args[0]) instanceof BlockPosition) {
                    BlockPosition blockPos = (BlockPosition)object;
                    object = args[1];
                    if (object instanceof IBlockData) {
                        IBlockData blockState = (IBlockData)object;
                        return this.setBlock(blockPos, blockState);
                    }
                }
                if (args.length < 2 || !((object = args[0]) instanceof BlockPosition)) break;
                BlockPosition blockPos = (BlockPosition)object;
                object = args[1];
                if (!(object instanceof Boolean)) break;
                Boolean bl = (Boolean)object;
                return this.removeBlock(blockPos, bl);
            }
            case "j": 
            case "addEntity": 
            case "addFreshEntity": {
                Object object;
                if (args.length < 1 || !((object = args[0]) instanceof Entity)) break;
                Entity entity = (Entity)object;
                return this.addEntity(entity);
            }
        }
        return method.invoke(this.serverLevel, args);
    }
}

