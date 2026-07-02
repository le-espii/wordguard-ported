/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.UnmodifiableIterator
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package org.enginehub.piston.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.Collection;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.logging.log4j.Logger;
import org.enginehub.piston.Command;
import org.enginehub.piston.CommandMetadata;
import org.enginehub.piston.CommandParseResult;
import org.enginehub.piston.config.ColorConfig;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ArgumentConverterAccess;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.exception.ConditionFailedException;
import org.enginehub.piston.exception.ConversionFailedException;
import org.enginehub.piston.exception.NoSuchFlagException;
import org.enginehub.piston.exception.UsageException;
import org.enginehub.piston.impl.ArgBindingImpl;
import org.enginehub.piston.impl.CommandInfo;
import org.enginehub.piston.impl.CommandInfoCache;
import org.enginehub.piston.impl.CommandParametersImpl;
import org.enginehub.piston.impl.CommandParseResultImpl;
import org.enginehub.piston.impl.CommandValueImpl;
import org.enginehub.piston.impl.LogManagerCompat;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.enginehub.piston.part.ArgAcceptingCommandFlag;
import org.enginehub.piston.part.ArgAcceptingCommandPart;
import org.enginehub.piston.part.ArgConsumingCommandPart;
import org.enginehub.piston.part.CommandArgument;
import org.enginehub.piston.part.CommandFlag;
import org.enginehub.piston.part.CommandPart;
import org.enginehub.piston.part.NoArgCommandFlag;
import org.enginehub.piston.part.SubCommandPart;
import org.enginehub.piston.util.ComponentHelper;
import org.enginehub.piston.util.StreamHelper;

class CommandParser {
    private static final Logger LOGGER = LogManagerCompat.getLogger();
    private static final ThreadLocal<String> PARSE_ID = new ThreadLocal();
    private final ArgumentConverterAccess converters;
    private final CommandMetadata metadata;
    private final CommandParseResultImpl.Builder parseResult = CommandParseResultImpl.builder();
    private final CommandParametersImpl.Builder parameters = CommandParametersImpl.builder();
    private final CommandInfoCache commandInfoCache;
    private final ImmutableList<String> arguments;
    private final ListIterator<String> argIter;
    private final InjectedValueAccess context;
    private final Set<CommandFlag> seenFlags = new HashSet<CommandFlag>();
    private ImmutableMap.Builder<CommandPart, Boolean> argBindings = ImmutableMap.builder();
    @Nullable
    private PerCommandDetails perCommandDetails;
    @Nullable
    private CommandArgument lastFailedOptional;
    @Nullable
    private CommandParseResult result;
    private boolean justUnconsumed;

    private static void log(String message, Object ... args) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[" + PARSE_ID.get() + "]: " + message, args);
        }
    }

    private static String newId() {
        long rng = ThreadLocalRandom.current().nextLong(0L, Long.MAX_VALUE);
        return Strings.padStart((String)Long.toString(rng, 36), (int)13, (char)'0');
    }

    private static void logParseStart() {
        PARSE_ID.set(CommandParser.newId());
        CommandParser.log("started parsing", new Object[0]);
    }

    CommandParser(ArgumentConverterAccess converters, CommandInfoCache commandInfoCache, Command initial, CommandMetadata metadata, InjectedValueAccess context) {
        this.commandInfoCache = commandInfoCache;
        this.converters = converters;
        this.metadata = metadata;
        this.arguments = metadata.getArguments();
        this.argIter = this.arguments.listIterator();
        this.context = context;
        this.switchToCommand(initial);
    }

    private void buildParseResult() {
        Preconditions.checkState((this.result == null ? 1 : 0) != 0, (Object)"Multiple calls to build final result");
        if (this.argBindings.build().size() > 0 && this.argIter.hasPrevious()) {
            this.bindArgument();
        }
        this.fillInDefaults();
        this.result = this.parseResult.parameters(this.parameters.metadata(this.metadata).injectedValues(this.context).converters(this.converters).build()).build();
    }

    private CommandParseResult getResult() {
        return (CommandParseResult)Preconditions.checkNotNull((Object)this.result, (Object)"Not finished parsing");
    }

    private UsageException usageException(Component message) {
        this.buildParseResult();
        return new UsageException(message, this.getResult());
    }

    private UsageException notEnoughArgumentsException() {
        return this.usageException(TextComponent.of("Not enough arguments."));
    }

    private UsageException tooManyArgumentsException() {
        return this.usageException(TextComponent.of("Too many arguments."));
    }

    private ConversionFailedException conversionFailedException(ArgAcceptingCommandPart nextArg, String token) {
        ArgumentConverter converter = nextArg.getTypes().stream().map(k -> this.converters.getConverter(k).orElse(null)).filter(Objects::nonNull).findFirst().orElseThrow(IllegalStateException::new);
        this.buildParseResult();
        return new ConversionFailedException(this.getResult(), nextArg.getTextRepresentation(), converter, (FailedConversion)converter.convert(token, this.context));
    }

    private ConditionFailedException conditionFailed() {
        this.buildParseResult();
        return new ConditionFailedException(this.getResult().getExecutionPath());
    }

    private boolean testCondition(Command.Condition condition) {
        return condition.satisfied(this.context);
    }

    private PerCommandDetails perCommandDetails() {
        return Objects.requireNonNull(this.perCommandDetails);
    }

    private String currentArgument() {
        int argumentIndex = this.argIter.previousIndex();
        if (argumentIndex < 0) {
            throw new IllegalStateException("No argument has been asked for yet");
        }
        return (String)this.arguments.get(argumentIndex);
    }

    private boolean hasNextArgument() {
        return this.argIter.hasNext();
    }

    private String nextArgument() {
        Preconditions.checkState((boolean)this.hasNextArgument(), (Object)"No next argument present, this call should be guarded with hasNextArgument");
        if (this.argIter.hasPrevious() && !this.justUnconsumed) {
            this.bindArgument();
        }
        this.justUnconsumed = false;
        String next = this.argIter.next();
        this.argBindings = ImmutableMap.builder();
        return next;
    }

    private void bindArgument() {
        ImmutableMap binding = this.argBindings.build();
        Preconditions.checkState((!binding.isEmpty() || this.currentArgument().equals("--") ? 1 : 0) != 0, (String)"Argument never bound: %s", (Object)this.currentArgument());
        this.parseResult.addArgument(ArgBindingImpl.builder().input(this.currentArgument()).partsMap((Map<CommandPart, Boolean>)binding).build());
    }

    private void unconsumeArgument() {
        Preconditions.checkState((boolean)this.argBindings.build().isEmpty(), (String)"Argument already bound: %s", (Object)this.currentArgument());
        Preconditions.checkState((boolean)this.argIter.hasPrevious(), (Object)"Trying to unconsume nothing");
        this.argIter.previous();
        this.justUnconsumed = true;
    }

    private int remainingNonFlagArguments() {
        return (int)this.arguments.stream().skip(this.argIter.previousIndex()).filter(s -> !this.isFlag((String)s)).count();
    }

    private boolean hasNextPart() {
        return this.perCommandDetails().partIter.hasNext();
    }

    private ArgConsumingCommandPart nextPart() {
        Preconditions.checkState((boolean)this.hasNextPart(), (Object)"No next part, this call should be guarded with hasNextPart()");
        return this.perCommandDetails().partIter.next();
    }

    private void unconsumePart() {
        ListIterator<ArgConsumingCommandPart> partIter = this.perCommandDetails().partIter;
        Preconditions.checkState((boolean)partIter.hasPrevious(), (Object)"Trying to unconsume nothing");
        partIter.previous();
    }

    private void bind(CommandPart part, boolean exact) {
        this.argBindings.put((Object)part, (Object)exact);
    }

    private void switchToCommand(Command subCommand) {
        if (this.perCommandDetails != null) {
            this.fillInDefaults();
        }
        this.parseResult.addCommand(subCommand);
        this.perCommandDetails = new PerCommandDetails(this.commandInfoCache.getInfo(subCommand));
        if (!this.testCondition(subCommand.getCondition())) {
            throw this.conditionFailed();
        }
    }

    private void fillInDefaults() {
        for (ArgAcceptingCommandPart part : this.perCommandDetails().defaultsNeeded) {
            this.addValueFull(part, v -> v.values((Collection<String>)part.getDefaults()));
        }
        for (ArgAcceptingCommandFlag flag : this.perCommandDetails().argFlagsNeeded) {
            this.addValueFull(flag, v -> v.value(""));
        }
    }

    private void finalizeCommand() {
        UnmodifiableIterator requiredIter;
        PerCommandDetails details = this.perCommandDetails();
        if (details.remainingRequiredParts > 0 && (requiredIter = Iterators.filter(details.partIter, CommandPart::isRequired)).hasNext()) {
            ArgConsumingCommandPart missing = (ArgConsumingCommandPart)requiredIter.next();
            if (missing instanceof CommandArgument) {
                throw this.usageException((Component)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder("Missing argument for ").append(missing.getTextRepresentation())).append((Component)TextComponent.of("."))).build());
            }
            Preconditions.checkState((boolean)(missing instanceof SubCommandPart), (String)"Unknown part interface: %s", missing.getClass());
            throw this.usageException(TextComponent.of("No sub-command provided. Options: " + ((SubCommandPart)missing).getCommands().stream().distinct().map(Command::getName).collect(Collectors.joining(", "))));
        }
    }

    CommandParseResult parse() {
        CommandParser.logParseStart();
        while (this.hasNextArgument()) {
            String token = this.nextArgument();
            CommandParser.log("Consuming argument `{}`", token);
            PerCommandDetails details = this.perCommandDetails();
            if (this.isFlag(token)) {
                if (token.equals("--")) {
                    CommandParser.log("Encountered `--`, turning off flag matching.", new Object[0]);
                    details.canMatchFlags = false;
                    continue;
                }
                this.parseFlags(token.substring(1));
                continue;
            }
            if (this.parseRegularArgument(token)) continue;
            if (this.lastFailedOptional != null) {
                throw this.conversionFailedException(this.lastFailedOptional, token);
            }
            throw this.tooManyArgumentsException();
        }
        CommandParser.log("Finished looking at arguments. Finalizing command.", new Object[0]);
        this.finalizeCommand();
        this.buildParseResult();
        return this.getResult();
    }

    private boolean isFlag(String token) {
        if (token.length() <= 1 || !this.perCommandDetails().canMatchFlags) {
            return false;
        }
        if (!token.startsWith("-")) {
            return false;
        }
        if (token.equals("--")) {
            return true;
        }
        return token.codePoints().skip(1L).allMatch(cp -> this.perCommandDetails().commandInfo.flags.containsKey((Object)Character.valueOf((char)cp)));
    }

    private boolean parseSubCommand(SubCommandPart part, String token) {
        CommandInfo commandInfo = this.perCommandDetails().commandInfo;
        ImmutableMap subCommands = commandInfo.subCommandTable.row((Object)part);
        Command sub = (Command)subCommands.get((Object)token);
        if (sub == null) {
            return false;
        }
        this.bind(part, true);
        if (part.isRequired()) {
            --this.perCommandDetails().remainingRequiredParts;
        }
        this.switchToCommand(sub);
        return true;
    }

    private TextComponent invalidSubCommandMessage(String token, ImmutableMap<String, Command> subCommands) {
        return (TextComponent)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder().append("Invalid sub-command '")).append(ColorConfig.mainText().wrap(token))).append("'. Options: ")).append(subCommands.values().stream().distinct().map(Command::getName).map(ColorConfig.mainText()::wrap).collect(ComponentHelper.joiningTexts(TextComponent.empty(), TextComponent.of(", "), TextComponent.empty())))).build();
    }

    private boolean parseRegularArgument(String token) {
        PerCommandDetails details = this.perCommandDetails();
        if (!this.hasNextPart()) {
            CommandParser.log("parseRegularArgument: no arguments to attempt matching", new Object[0]);
        }
        CommandArgument lastFailedOptionalLocal = null;
        while (this.hasNextPart()) {
            AcceptInfo acceptInfo;
            ArgConsumingCommandPart nextArg = this.nextPart();
            if (nextArg instanceof SubCommandPart) {
                SubCommandPart subCommandPart = (SubCommandPart)nextArg;
                if (this.parseSubCommand(subCommandPart, token)) {
                    return true;
                }
                if (!nextArg.isRequired()) continue;
                throw this.usageException(this.invalidSubCommandMessage(token, (ImmutableMap<String, Command>)details.commandInfo.subCommandTable.row((Object)subCommandPart)));
            }
            Preconditions.checkState((boolean)(nextArg instanceof CommandArgument), (String)"Unknown part interface: %s", nextArg.getClass());
            CommandArgument argPart = (CommandArgument)nextArg;
            if (nextArg.isRequired()) {
                AcceptInfo acceptInfo2 = this.getAcceptInfoFromTypeParsers(argPart, token);
                if (!acceptInfo2.isAccepted()) {
                    throw this.conversionFailedException(argPart, token);
                }
                --details.remainingRequiredParts;
                this.addValueFull(nextArg, v -> v.values((Collection<String>)this.consumeArguments(argPart, token, acceptInfo2 == AcceptInfo.ACCEPTED_EXACT)));
                return true;
            }
            if (details.commandInfo.subCommandTable.isEmpty()) {
                int remainingArguments = this.remainingNonFlagArguments();
                int diff = remainingArguments - details.remainingRequiredParts;
                if (diff < 0) {
                    throw this.notEnoughArgumentsException();
                }
                if (diff == 0) continue;
            }
            if ((acceptInfo = this.getAcceptInfoFromTypeParsers(argPart, token)).isAccepted()) {
                details.defaultsNeeded.remove(nextArg);
                this.addValueFull(nextArg, v -> v.values((Collection<String>)this.consumeArguments(argPart, token, acceptInfo == AcceptInfo.ACCEPTED_EXACT)));
                return true;
            }
            lastFailedOptionalLocal = argPart;
        }
        this.lastFailedOptional = lastFailedOptionalLocal;
        return false;
    }

    private ImmutableList<String> consumeArguments(CommandArgument nextArg, String first, boolean exact) {
        ImmutableList.Builder result = ImmutableList.builder();
        this.bind(nextArg, exact);
        result.add((Object)first);
        if (nextArg.isVariable()) {
            while (this.hasNextArgument()) {
                String next = this.nextArgument();
                AcceptInfo acceptInfo = this.getAcceptInfoFromTypeParsers(nextArg, next);
                if (acceptInfo.isAccepted()) {
                    this.bind(nextArg, acceptInfo == AcceptInfo.ACCEPTED_EXACT);
                    result.add((Object)next);
                    continue;
                }
                this.unconsumeArgument();
                break;
            }
        }
        return result.build();
    }

    private AcceptInfo getAcceptInfoFromTypeParsers(ArgAcceptingCommandPart part, String next) {
        ImmutableSet<Key<?>> types = part.getTypes();
        if (types.isEmpty()) {
            return AcceptInfo.ACCEPTED_EXACT;
        }
        boolean acceptedInexact = false;
        for (Key type : types) {
            Optional argumentConverter = this.converters.getConverter(type);
            if (!argumentConverter.isPresent()) {
                throw new IllegalStateException("No argument converter for " + type);
            }
            ConversionResult result = argumentConverter.get().convert(next, this.context);
            if (!result.isSuccessful()) continue;
            if (((SuccessfulConversion)result).isExactMatch()) {
                return AcceptInfo.ACCEPTED_EXACT;
            }
            acceptedInexact = true;
        }
        return acceptedInexact ? AcceptInfo.ACCEPTED_INEXACT : AcceptInfo.REJECTED;
    }

    private void parseFlags(String flags) {
        for (int i = 0; i < flags.length(); ++i) {
            char c = flags.charAt(i);
            CommandFlag flag = (CommandFlag)this.perCommandDetails().commandInfo.flags.get((Object)Character.valueOf(c));
            if (flag == null) {
                this.buildParseResult();
                throw new NoSuchFlagException(this.getResult(), c);
            }
            if (this.seenFlags.contains(flag)) {
                throw this.usageException((Component)((TextComponent.Builder)((TextComponent.Builder)TextComponent.builder("Flag ").append(flag.getTextRepresentation())).append(" has already been specified.")).build());
            }
            if (flag instanceof ArgAcceptingCommandFlag) {
                if (i + 1 < flags.length()) {
                    throw this.usageException(TextComponent.of("Argument-accepting flags must be at the end of combined flag groups."));
                }
                this.bind(flag, true);
                ArgAcceptingCommandFlag argPart = (ArgAcceptingCommandFlag)flag;
                if (!this.hasNextArgument()) {
                    throw this.notEnoughArgumentsException();
                }
                String nextToken = this.nextArgument();
                AcceptInfo acceptInfo = this.getAcceptInfoFromTypeParsers(argPart, nextToken);
                if (!acceptInfo.isAccepted()) {
                    throw this.conversionFailedException(argPart, nextToken);
                }
                this.addValueFull(flag, v -> v.value(nextToken));
                this.bind(flag, acceptInfo == AcceptInfo.ACCEPTED_EXACT);
                this.perCommandDetails().defaultsNeeded.remove(flag);
                this.perCommandDetails().argFlagsNeeded.remove(flag);
            } else {
                Preconditions.checkState((boolean)(flag instanceof NoArgCommandFlag));
                this.bind(flag, true);
                this.parameters.addPresentPart(flag);
            }
            this.seenFlags.add(flag);
        }
    }

    private void addValueFull(CommandPart part, Consumer<CommandValueImpl.Builder> valueAdder) {
        this.parameters.addPresentPart(part);
        CommandValueImpl.Builder builder = CommandValueImpl.builder();
        valueAdder.accept(builder);
        this.parameters.addValue(part, builder.commandContextSupplier(this::getResult).partContext(part).injectedValues(this.context).manager(this.converters).build());
    }

    private static final class PerCommandDetails {
        final CommandInfo commandInfo;
        final Set<ArgAcceptingCommandPart> defaultsNeeded;
        final Set<ArgAcceptingCommandFlag> argFlagsNeeded;
        final ListIterator<ArgConsumingCommandPart> partIter;
        boolean canMatchFlags = true;
        int remainingRequiredParts;

        private PerCommandDetails(CommandInfo commandInfo) {
            this.commandInfo = commandInfo;
            this.defaultsNeeded = new HashSet<ArgAcceptingCommandPart>((Collection<ArgAcceptingCommandPart>)commandInfo.defaultProvided);
            this.argFlagsNeeded = StreamHelper.cast(commandInfo.flags.values().stream(), ArgAcceptingCommandFlag.class).filter(arg -> !this.defaultsNeeded.contains(arg)).collect(Collectors.toCollection(HashSet::new));
            this.partIter = commandInfo.arguments.listIterator();
            this.remainingRequiredParts = commandInfo.requiredParts;
        }
    }

    private static enum AcceptInfo {
        REJECTED,
        ACCEPTED_INEXACT,
        ACCEPTED_EXACT;


        boolean isAccepted() {
            return this != REJECTED;
        }
    }
}

