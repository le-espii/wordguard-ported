/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.commands.task;

import com.google.common.base.Preconditions;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

public class RegionAdder
implements Callable<ProtectedRegion> {
    private final RegionManager manager;
    private final ProtectedRegion region;
    @Nullable
    private String[] ownersInput;
    private DomainInputResolver.UserLocatorPolicy locatorPolicy = DomainInputResolver.UserLocatorPolicy.UUID_ONLY;

    public RegionAdder(RegionManager manager, ProtectedRegion region) {
        Preconditions.checkNotNull((Object)manager);
        Preconditions.checkNotNull((Object)region);
        this.manager = manager;
        this.region = region;
    }

    public void addOwnersFromCommand(CommandContext args, int namesIndex) {
        if (args.argsLength() >= namesIndex) {
            this.setLocatorPolicy(args.hasFlag('n') ? DomainInputResolver.UserLocatorPolicy.NAME_ONLY : DomainInputResolver.UserLocatorPolicy.UUID_ONLY);
            this.setOwnersInput(args.getSlice(namesIndex));
        }
    }

    @Override
    public ProtectedRegion call() throws Exception {
        if (this.ownersInput != null) {
            DomainInputResolver resolver = new DomainInputResolver(WorldGuard.getInstance().getProfileService(), this.ownersInput);
            resolver.setLocatorPolicy(this.locatorPolicy);
            DefaultDomain domain = resolver.call();
            this.region.getOwners().addAll(domain);
        }
        this.manager.addRegion(this.region);
        return this.region;
    }

    @Nullable
    public String[] getOwnersInput() {
        return this.ownersInput;
    }

    public void setOwnersInput(@Nullable String[] ownersInput) {
        this.ownersInput = ownersInput;
    }

    public DomainInputResolver.UserLocatorPolicy getLocatorPolicy() {
        return this.locatorPolicy;
    }

    public void setLocatorPolicy(DomainInputResolver.UserLocatorPolicy locatorPolicy) {
        this.locatorPolicy = locatorPolicy;
    }
}

