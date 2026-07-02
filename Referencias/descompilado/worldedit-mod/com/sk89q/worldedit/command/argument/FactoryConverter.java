/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.command.argument;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseItem;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.internal.annotation.ClipboardMask;
import com.sk89q.worldedit.internal.registry.AbstractFactory;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.request.RequestExtent;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.enginehub.piston.CommandManager;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;

public class FactoryConverter<T>
implements ArgumentConverter<T> {
    private final WorldEdit worldEdit;
    private final Function<WorldEdit, AbstractFactory<T>> factoryExtractor;
    private final String description;
    @Nullable
    private final Consumer<ParserContext> contextTweaker;

    public static void register(WorldEdit worldEdit, CommandManager commandManager) {
        commandManager.registerConverter(Key.of(Pattern.class), new FactoryConverter(worldEdit, WorldEdit::getPatternFactory, "pattern", null));
        commandManager.registerConverter(Key.of(Mask.class), new FactoryConverter(worldEdit, WorldEdit::getMaskFactory, "mask", null));
        commandManager.registerConverter(Key.of(BaseItem.class), new FactoryConverter(worldEdit, WorldEdit::getItemFactory, "item", null));
        commandManager.registerConverter(Key.of(Mask.class, ClipboardMask.class), new FactoryConverter(worldEdit, WorldEdit::getMaskFactory, "mask", context -> {
            try {
                ClipboardHolder holder = context.getSession().getClipboard();
                Transform transform = holder.getTransform();
                Extent target = transform.isIdentity() ? holder.getClipboard() : new BlockTransformExtent(holder.getClipboard(), transform);
                context.setExtent(target);
            }
            catch (EmptyClipboardException e) {
                throw new IllegalStateException(e);
            }
        }));
    }

    private FactoryConverter(WorldEdit worldEdit, Function<WorldEdit, AbstractFactory<T>> factoryExtractor, String description, @Nullable Consumer<ParserContext> contextTweaker) {
        this.worldEdit = worldEdit;
        this.factoryExtractor = factoryExtractor;
        this.description = description;
        this.contextTweaker = contextTweaker;
    }

    private ParserContext createContext(InjectedValueAccess context) {
        Actor actor = context.injectedValue(Key.of(Actor.class)).orElseThrow(() -> new IllegalStateException("No actor"));
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(actor);
        ParserContext parserContext = new ParserContext();
        parserContext.setActor(actor);
        if (actor instanceof Locatable) {
            Extent extent = ((Locatable)((Object)actor)).getExtent();
            if (extent instanceof World) {
                parserContext.setWorld((World)extent);
            }
            parserContext.setExtent(new RequestExtent());
        } else if (session.hasWorldOverride()) {
            parserContext.setWorld(session.getWorldOverride());
            parserContext.setExtent(new RequestExtent());
        }
        parserContext.setSession(session);
        parserContext.setRestricted(true);
        if (this.contextTweaker != null) {
            this.contextTweaker.accept(parserContext);
        }
        return parserContext;
    }

    @Override
    public ConversionResult<T> convert(String argument, InjectedValueAccess context) {
        ParserContext parserContext = this.createContext(context);
        try {
            return SuccessfulConversion.fromSingle(this.factoryExtractor.apply(this.worldEdit).parseFromInput(argument, parserContext));
        }
        catch (InputParseException e) {
            return FailedConversion.from(e);
        }
    }

    @Override
    public List<String> getSuggestions(String input, InjectedValueAccess context) {
        ParserContext parserContext = this.createContext(context);
        return this.factoryExtractor.apply(this.worldEdit).getSuggestions(input, parserContext);
    }

    @Override
    public Component describeAcceptableArguments() {
        return TextComponent.of("any " + this.description);
    }
}

