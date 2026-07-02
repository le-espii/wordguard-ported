/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1937
 *  net.minecraft.class_2338
 *  net.minecraft.class_2586
 *  net.minecraft.class_2680
 *  net.minecraft.class_3218
 *  net.minecraft.class_5281
 *  net.minecraft.class_7225$class_7874
 *  org.jetbrains.annotations.Nullable
 */
package com.sk89q.worldedit.fabric.internal;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.internal.FabricEntity;
import com.sk89q.worldedit.fabric.internal.NBTConverter;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_5281;
import net.minecraft.class_7225;
import org.jetbrains.annotations.Nullable;

public class FabricServerLevelDelegateProxy
implements InvocationHandler {
    private final EditSession editSession;
    private final class_3218 serverLevel;

    private FabricServerLevelDelegateProxy(EditSession editSession, class_3218 serverLevel) {
        this.editSession = editSession;
        this.serverLevel = serverLevel;
    }

    public static class_5281 newInstance(EditSession editSession, class_3218 serverLevel) {
        return (class_5281)Proxy.newProxyInstance(serverLevel.getClass().getClassLoader(), serverLevel.getClass().getInterfaces(), new FabricServerLevelDelegateProxy(editSession, serverLevel));
    }

    @Nullable
    private class_2586 getBlockEntity(class_2338 blockPos) {
        class_2586 tileEntity = this.serverLevel.method_8500(blockPos).method_8321(blockPos);
        if (tileEntity == null) {
            return null;
        }
        class_2586 newEntity = tileEntity.method_11017().method_11032(blockPos, this.getBlockState(blockPos));
        newEntity.method_58690(NBTConverter.toNative(this.editSession.getFullBlock(FabricAdapter.adapt(blockPos)).getNbtReference().getValue()), (class_7225.class_7874)this.serverLevel.method_30349());
        return newEntity;
    }

    private class_2680 getBlockState(class_2338 blockPos) {
        return FabricAdapter.adapt(this.editSession.getBlock(FabricAdapter.adapt(blockPos)));
    }

    private boolean setBlock(class_2338 blockPos, class_2680 blockState) {
        try {
            return this.editSession.setBlock(FabricAdapter.adapt(blockPos), FabricAdapter.adapt(blockState));
        }
        catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean removeBlock(class_2338 blockPos, boolean bl) {
        try {
            return this.editSession.setBlock(FabricAdapter.adapt(blockPos), BlockTypes.AIR.getDefaultState());
        }
        catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean addEntity(class_1297 entity) {
        BaseEntity baseEntity;
        Vector3 pos = FabricAdapter.adapt(entity.method_30950(0.0f));
        Location location = new Location((Extent)FabricAdapter.adapt((class_1937)this.serverLevel), pos.x(), pos.y(), pos.z());
        return this.editSession.createEntity(location, baseEntity = new FabricEntity(entity).getState()) != null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "getBlockState": 
            case "method_8320": {
                Object object;
                if (args.length != 1 || !((object = args[0]) instanceof class_2338)) break;
                class_2338 blockPos = (class_2338)object;
                return this.getBlockState(blockPos);
            }
            case "getBlockEntity": 
            case "method_8321": {
                Object object;
                if (args.length != 1 || !((object = args[0]) instanceof class_2338)) break;
                class_2338 blockPos = (class_2338)object;
                return this.getBlockEntity(blockPos);
            }
            case "setBlock": 
            case "method_8652": {
                Object object;
                if (args.length < 2 || !((object = args[0]) instanceof class_2338)) break;
                class_2338 blockPos = (class_2338)object;
                object = args[1];
                if (!(object instanceof class_2680)) break;
                class_2680 blockState = (class_2680)object;
                return this.setBlock(blockPos, blockState);
            }
            case "removeBlock": 
            case "destroyBlock": 
            case "method_8650": 
            case "method_8651": {
                Object object;
                if (args.length < 2 || !((object = args[0]) instanceof class_2338)) break;
                class_2338 blockPos = (class_2338)object;
                object = args[1];
                if (!(object instanceof Boolean)) break;
                Boolean bl = (Boolean)object;
                return this.removeBlock(blockPos, bl);
            }
            case "addEntity": 
            case "method_14175": 
            case "addFreshEntityWithPassengers": 
            case "method_30771": {
                Object object;
                if (args.length < 1 || !((object = args[0]) instanceof class_1297)) break;
                class_1297 entity = (class_1297)object;
                return this.addEntity(entity);
            }
        }
        return method.invoke(this.serverLevel, args);
    }
}

