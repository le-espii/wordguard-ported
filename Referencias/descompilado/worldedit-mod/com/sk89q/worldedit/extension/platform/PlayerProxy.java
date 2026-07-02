/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.extension.platform;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.cui.CUIEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.HandSide;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.gamemode.GameMode;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;

class PlayerProxy
extends AbstractPlayerActor {
    private final Player basePlayer;
    private final Actor permActor;
    private final Actor cuiActor;
    private final World world;

    PlayerProxy(Player basePlayer, Actor permActor, Actor cuiActor, World world) {
        Preconditions.checkNotNull((Object)basePlayer);
        Preconditions.checkNotNull((Object)permActor);
        Preconditions.checkNotNull((Object)cuiActor);
        Preconditions.checkNotNull((Object)world);
        this.basePlayer = basePlayer;
        this.permActor = permActor;
        this.cuiActor = cuiActor;
        this.world = world;
    }

    @Override
    public UUID getUniqueId() {
        return this.basePlayer.getUniqueId();
    }

    @Override
    public BaseItemStack getItemInHand(HandSide handSide) {
        return this.basePlayer.getItemInHand(handSide);
    }

    @Override
    public void giveItem(BaseItemStack itemStack) {
        this.basePlayer.giveItem(itemStack);
    }

    @Override
    public BlockBag getInventoryBlockBag() {
        return this.basePlayer.getInventoryBlockBag();
    }

    @Override
    public String getName() {
        return this.basePlayer.getName();
    }

    @Override
    public String getDisplayName() {
        return this.basePlayer.getDisplayName();
    }

    @Override
    public BaseEntity getState() {
        throw new UnsupportedOperationException("Can't getState() on a player");
    }

    @Override
    public Location getLocation() {
        return this.basePlayer.getLocation();
    }

    @Override
    public boolean setLocation(Location location) {
        return this.basePlayer.setLocation(location);
    }

    @Override
    public boolean trySetPosition(Vector3 pos, float pitch, float yaw) {
        return this.basePlayer.trySetPosition(pos, pitch, yaw);
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    @Deprecated
    public void printRaw(String msg) {
        this.basePlayer.print(TextComponent.of(msg));
    }

    @Override
    @Deprecated
    public void printDebug(String msg) {
        this.basePlayer.printDebug(TextComponent.of(msg));
    }

    @Override
    @Deprecated
    public void print(String msg) {
        this.basePlayer.printInfo(TextComponent.of(msg));
    }

    @Override
    @Deprecated
    public void printError(String msg) {
        this.basePlayer.printError(TextComponent.of(msg));
    }

    @Override
    public void print(Component component) {
        this.basePlayer.print(component);
    }

    @Override
    public String[] getGroups() {
        return this.permActor.getGroups();
    }

    @Override
    public boolean hasPermission(String perm) {
        return this.permActor.hasPermission(perm);
    }

    @Override
    public void dispatchCUIEvent(CUIEvent event) {
        this.cuiActor.dispatchCUIEvent(event);
    }

    @Override
    public void sendAnnouncements() {
        this.basePlayer.sendAnnouncements();
    }

    @Override
    @Nullable
    public <T> T getFacet(Class<? extends T> cls) {
        return this.basePlayer.getFacet(cls);
    }

    @Override
    public SessionKey getSessionKey() {
        return this.basePlayer.getSessionKey();
    }

    @Override
    public GameMode getGameMode() {
        return this.basePlayer.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.basePlayer.setGameMode(gameMode);
    }

    @Override
    public <B extends BlockStateHolder<B>> void sendFakeBlock(BlockVector3 pos, B block) {
        this.basePlayer.sendFakeBlock(pos, block);
    }

    @Override
    public void floatAt(int x, int y, int z, boolean alwaysGlass) {
        this.basePlayer.floatAt(x, y, z, alwaysGlass);
    }

    @Override
    public Locale getLocale() {
        return this.basePlayer.getLocale();
    }
}

