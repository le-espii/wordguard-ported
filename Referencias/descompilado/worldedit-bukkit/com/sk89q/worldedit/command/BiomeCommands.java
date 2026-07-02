/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.enginehub.piston.annotation.Command
 *  org.enginehub.piston.annotation.CommandContainer
 *  org.enginehub.piston.annotation.param.Arg
 *  org.enginehub.piston.annotation.param.ArgFlag
 *  org.enginehub.piston.annotation.param.Switch
 */
package com.sk89q.worldedit.command;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.Logging;
import com.sk89q.worldedit.command.util.WorldEditAsyncCommandBuilder;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.component.PaginationBox;
import com.sk89q.worldedit.util.formatting.component.TextUtils;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.registry.BiomeRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.annotation.param.Switch;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class BiomeCommands {
    @Command(name="biomelist", aliases={"biomels"}, desc="Gets all biomes available.")
    @CommandPermissions(value={"worldedit.biome.list"})
    public void biomeList(Actor actor, @ArgFlag(name=112, desc="Page number.", def={"1"}) int page) {
        WorldEditAsyncCommandBuilder.createAndSendMessage(actor, () -> {
            BiomeRegistry biomeRegistry = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getBiomeRegistry();
            PaginationBox paginationBox = PaginationBox.fromComponents("Available Biomes", "/biomelist -p %page%", BiomeType.REGISTRY.values().stream().map(biomeType -> (TextComponent)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder().append(biomeType.id())).append(" (")).append(biomeRegistry.getRichName((BiomeType)biomeType))).append(")")).build()).collect(Collectors.toList()));
            return paginationBox.create(page);
        }, (Component)null);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Command(name="biomeinfo", desc="Get the biome of the targeted block.", descFooter="By default, uses all blocks in your selection.")
    @CommandPermissions(value={"worldedit.biome.info"})
    public void biomeInfo(Actor actor, World world, LocalSession session, @Switch(name=116, desc="Use the block you are looking at.") boolean useLineOfSight, @Switch(name=112, desc="Use the block you are currently in.") boolean usePosition) throws WorldEditException {
        String messageKey;
        BiomeRegistry biomeRegistry = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getRegistries().getBiomeRegistry();
        HashSet<BiomeType> biomes = new HashSet<BiomeType>();
        if (useLineOfSight) {
            if (!(actor instanceof Player)) {
                actor.printError(TranslatableComponent.of("worldedit.raytrace.require-player"));
                return;
            }
            Location blockPosition = ((Player)actor).getBlockTrace(300);
            if (blockPosition == null) {
                actor.printError(TranslatableComponent.of("worldedit.raytrace.noblock"));
                return;
            }
            BiomeType biome2 = world.getBiome(blockPosition.toVector().toBlockPoint());
            biomes.add(biome2);
            messageKey = "worldedit.biomeinfo.lineofsight";
        } else if (usePosition) {
            if (!(actor instanceof Locatable)) {
                actor.printError(TranslatableComponent.of("worldedit.biomeinfo.not-locatable"));
                return;
            }
            BiomeType biome3 = world.getBiome(((Locatable)((Object)actor)).getLocation().toVector().toBlockPoint());
            biomes.add(biome3);
            messageKey = "worldedit.biomeinfo.position";
        } else {
            Region region = session.getSelection(world);
            for (BlockVector3 pt : region) {
                biomes.add(world.getBiome(pt));
            }
            messageKey = "worldedit.biomeinfo.selection";
        }
        List<Component> components = biomes.stream().map(biome -> biomeRegistry.getRichName((BiomeType)biome).hoverEvent(HoverEvent.showText(TextComponent.of(biome.id())))).collect(Collectors.toList());
        actor.printInfo(TranslatableComponent.of(messageKey, TextUtils.join(components, TextComponent.of(", "))));
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     */
    @Command(name="/setbiome", desc="Sets the biome of your current block or region.", descFooter="By default, uses all the blocks in your selection")
    @Logging(value=Logging.LogMode.REGION)
    @CommandPermissions(value={"worldedit.biome.set"})
    public void setBiome(Actor actor, World world, LocalSession session, EditSession editSession, @Arg(desc="Biome type.") BiomeType target, @Switch(name=112, desc="Use your current position") boolean atPosition) throws WorldEditException {
        void var9_11;
        Region region;
        Mask mask = editSession.getMask();
        if (atPosition) {
            if (!(actor instanceof Locatable)) {
                actor.printError(TranslatableComponent.of("worldedit.setbiome.not-locatable"));
                return;
            }
            BlockVector3 blockVector3 = ((Locatable)((Object)actor)).getLocation().toVector().toBlockPoint();
            region = new CuboidRegion(blockVector3, blockVector3);
        } else {
            region = session.getSelection(world);
        }
        BiomeReplace biomeReplace = new BiomeReplace((Extent)editSession, target);
        if (mask != null) {
            RegionMaskingFilter regionMaskingFilter = new RegionMaskingFilter(mask, biomeReplace);
        }
        RegionVisitor visitor = new RegionVisitor(region, (RegionFunction)var9_11);
        Operations.completeLegacy(visitor);
        actor.printInfo(((TranslatableComponent)TranslatableComponent.of("worldedit.setbiome.changed", TextComponent.of(visitor.getAffected())).append(TextComponent.newline())).append(TranslatableComponent.of("worldedit.setbiome.warning")));
    }
}

