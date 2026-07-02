/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package org.enginehub.piston.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.enginehub.piston.Command;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.NoArgCommandFlag;
import org.enginehub.piston.part.SubCommandPart;
import org.enginehub.piston.util.ComponentHelper;

public class PartHelper {
    public static void appendUsage(Stream<CommandPart> parts, TextComponent.Builder output) {
        Stream.Builder<Component> other = Stream.builder();
        TreeSet<Character> flags = new TreeSet<Character>();
        Iterator iterator = parts.iterator();
        Stream.Builder postOSC = Stream.builder();
        SubCommandPart optionalSubCommand = null;
        while (iterator.hasNext()) {
            CommandPart part = (CommandPart)iterator.next();
            if (part instanceof NoArgCommandFlag) {
                Preconditions.checkState((optionalSubCommand == null ? 1 : 0) != 0, (Object)"All flags should come before sub-commands.");
                flags.add(Character.valueOf(((NoArgCommandFlag)part).getName()));
                continue;
            }
            if (part instanceof SubCommandPart && !part.isRequired()) {
                optionalSubCommand = (SubCommandPart)part;
                continue;
            }
            (optionalSubCommand == null ? other : postOSC).add(part.getTextRepresentation());
        }
        Stream flagsString = Optional.of(flags).filter(x -> !x.isEmpty()).map(f -> f.stream().map(String::valueOf).collect(Collectors.joining(""))).map(text -> ColorConfig.partWrapping().wrap(TextComponent.of("["), ColorConfig.mainText().wrap("-" + text), TextComponent.of("]"))).map(Stream::of).orElse(Stream.empty());
        Stream afterFlags = optionalSubCommand == null ? other.build() : Stream.concat(other.build(), PartHelper.buildOptionalMerging(optionalSubCommand, postOSC.build()));
        Iterator usages = Stream.concat(flagsString, afterFlags).iterator();
        while (usages.hasNext()) {
            output.append((Component)usages.next());
            if (!usages.hasNext()) continue;
            output.append((Component)TextComponent.of(" "));
        }
    }

    private static Stream<Component> buildOptionalMerging(SubCommandPart optionalSubCommand, Stream<Component> postComponents) {
        Component[] componentArray = new Component[5];
        componentArray[0] = TextComponent.of("<");
        componentArray[1] = optionalSubCommand.getCommands().stream().map(Command::getName).map(ColorConfig.mainText()::wrap).collect(ComponentHelper.joiningWithBar());
        componentArray[2] = TextComponent.of("|");
        componentArray[3] = postComponents.collect(ComponentHelper.joiningTexts(TextComponent.empty(), TextComponent.of(" "), TextComponent.empty()));
        componentArray[4] = TextComponent.of(">");
        return Stream.of(ColorConfig.partWrapping().wrap(componentArray));
    }
}

