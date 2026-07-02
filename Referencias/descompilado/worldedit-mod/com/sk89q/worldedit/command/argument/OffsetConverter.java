/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.sk89q.worldedit.command.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.command.argument.DirectionVectorConverter;
import com.sk89q.worldedit.command.argument.VectorConverter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.internal.annotation.Offset;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.MathUtils;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.List;
import java.util.stream.Collectors;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class OffsetConverter
implements ArgumentConverter<BlockVector3> {
    private final DirectionVectorConverter directionVectorConverter;
    private final VectorConverter<Integer, BlockVector3> vectorConverter = VectorConverter.BLOCK_VECTOR_3_CONVERTER;

    public static void register(WorldEdit worldEdit, CommandManager commandManager) {
        commandManager.registerConverter(Key.of(BlockVector3.class, Offset.class), new OffsetConverter(worldEdit));
    }

    private OffsetConverter(WorldEdit worldEdit) {
        this.directionVectorConverter = new DirectionVectorConverter(worldEdit, true);
    }

    @Override
    public Component describeAcceptableArguments() {
        return ((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder().append(this.directionVectorConverter.describeAcceptableArguments())).append(", or ")).append(this.vectorConverter.describeAcceptableArguments())).build();
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        if (input.startsWith("^") && context.injectedValue(Key.of(Actor.class)).filter(actor -> actor instanceof Locatable).isPresent()) {
            return this.vectorConverter.getSuggestions(input.substring(1), context).stream().map(s -> "^" + s).collect(Collectors.toList());
        }
        return ImmutableList.copyOf((Iterable)Iterables.concat(this.directionVectorConverter.getSuggestions(input, context), this.vectorConverter.getSuggestions(input, context)));
    }

    private BlockVector3 rotateToRelative(Location location, BlockVector3 relativeOffset) {
        float pitch = location.getPitch();
        float yaw = location.getYaw();
        double f = MathUtils.dCos((double)yaw + 90.0);
        double g = MathUtils.dSin((double)yaw + 90.0);
        double j = MathUtils.dCos((double)(-pitch) + 90.0);
        double k = MathUtils.dSin((double)(-pitch) + 90.0);
        Vector3 m1 = location.getDirection();
        Vector3 m2 = Vector3.at(f * j, k, g * j);
        Vector3 m3 = m1.cross(m2).multiply(-1.0);
        AffineTransform transform = new AffineTransform(m1.x(), m2.x(), m3.x(), 0.0, m1.y(), m2.y(), m3.y(), 0.0, m1.z(), m2.z(), m3.z(), 0.0);
        return transform.apply(relativeOffset.toVector3()).round().toBlockPoint();
    }

    @Override
    public ConversionResult<BlockVector3> convert(String input, InjectedValueAccess context) {
        if (input.startsWith("^")) {
            try {
                Actor actor = context.injectedValue(Key.of(Actor.class)).orElseThrow(() -> new IllegalStateException("An actor is required to use relative offsets"));
                if (!(actor instanceof Locatable)) {
                    throw new IllegalStateException("Only a locatable actor may use relative offsets");
                }
                Location location = ((Locatable)((Object)actor)).getLocation();
                return this.vectorConverter.convert(input.substring(1), context).map(blockVector3s -> blockVector3s.stream().map(vector -> this.rotateToRelative(location, (BlockVector3)((Object)((Object)vector)))).collect(Collectors.toList()));
            }
            catch (IllegalStateException e) {
                return FailedConversion.from(e);
            }
        }
        return this.directionVectorConverter.convert(input, context).orElse(this.vectorConverter.convert(input, context));
    }
}

