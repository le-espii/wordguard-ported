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

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.command.util.CommandPermissions;
import com.sk89q.worldedit.command.util.CommandPermissionsConditionGenerator;
import com.sk89q.worldedit.command.util.HookMode;
import com.sk89q.worldedit.command.util.PrintCommandHelp;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.platform.ConfigurationLoadEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.PlatformManager;
import com.sk89q.worldedit.util.formatting.component.MessageBox;
import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.util.formatting.text.event.HoverEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.paste.ActorCallbackPaste;
import com.sk89q.worldedit.util.paste.PasteMetadata;
import com.sk89q.worldedit.util.report.ConfigReport;
import com.sk89q.worldedit.util.report.ReportList;
import com.sk89q.worldedit.util.report.SystemInfoReport;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.zone.ZoneRulesException;
import java.util.List;
import org.enginehub.piston.annotation.Command;
import org.enginehub.piston.annotation.CommandContainer;
import org.enginehub.piston.annotation.param.Arg;
import org.enginehub.piston.annotation.param.ArgFlag;
import org.enginehub.piston.annotation.param.Switch;

@CommandContainer(superTypes={CommandPermissionsConditionGenerator.Registration.class})
public class WorldEditCommands {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private final WorldEdit we;

    public WorldEditCommands(WorldEdit we) {
        this.we = we;
    }

    @Command(name="version", aliases={"ver"}, desc="Get WorldEdit version")
    public void version(Actor actor) {
        actor.printInfo(TranslatableComponent.of("worldedit.version.version", TextComponent.of(WorldEdit.getVersion())));
        actor.printInfo(TextComponent.of("https://github.com/EngineHub/WorldEdit/"));
        PlatformManager pm = this.we.getPlatformManager();
        TextComponentProducer producer = new TextComponentProducer();
        for (Platform platform : pm.getPlatforms()) {
            producer.append(((TextComponent)TextComponent.of("* ", TextColor.GRAY).append(TextComponent.of(platform.getPlatformName()).hoverEvent(HoverEvent.showText(TextComponent.of(platform.id()))))).append(TextComponent.of("(" + platform.getPlatformVersion() + ")"))).newline();
        }
        actor.print(new MessageBox("Platforms", producer, TextColor.GRAY).create());
        producer.reset();
        for (Capability capability : Capability.values()) {
            Platform platform = pm.queryCapability(capability);
            producer.append(TextComponent.of(capability.name(), TextColor.GRAY).append(TextComponent.of(": ").append(TextComponent.of(platform != null ? platform.getPlatformName() : "none")))).newline();
        }
        actor.print(new MessageBox("Capabilities", producer, TextColor.GRAY).create());
    }

    @Command(name="reload", desc="Reload configuration")
    @CommandPermissions(value={"worldedit.reload"})
    public void reload(Actor actor) {
        this.we.getPlatformManager().queryCapability(Capability.CONFIGURATION).reload();
        this.we.getEventBus().post(new ConfigurationLoadEvent(this.we.getPlatformManager().queryCapability(Capability.CONFIGURATION).getConfiguration()));
        actor.printInfo(TranslatableComponent.of("worldedit.reload.config"));
    }

    @Command(name="report", desc="Writes a report on WorldEdit")
    @CommandPermissions(value={"worldedit.report"})
    public void report(Actor actor, @Switch(name=112, desc="Pastebins the report") boolean pastebin) throws WorldEditException {
        ReportList report = new ReportList("Report");
        report.add(new SystemInfoReport());
        report.add(new ConfigReport());
        String result = report.toString();
        try {
            Path dest = this.we.getConfiguration().getWorkingDirectoryPath().resolve("report.txt");
            Files.writeString((Path)dest, (CharSequence)result, (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);
            actor.printInfo(TranslatableComponent.of("worldedit.report.written", TextComponent.of(dest.toAbsolutePath().toString())));
        }
        catch (IOException e) {
            actor.printError(TranslatableComponent.of("worldedit.report.error", TextComponent.of(e.getMessage())));
        }
        if (pastebin) {
            actor.checkPermission("worldedit.report.pastebin");
            PasteMetadata metadata = new PasteMetadata();
            metadata.author = actor.getName();
            metadata.extension = "report";
            ActorCallbackPaste.pastebin(this.we.getSupervisor(), actor, result, metadata, TranslatableComponent.builder("worldedit.report.callback"));
        }
    }

    @Command(name="trace", desc="Toggles trace hook")
    void trace(Actor actor, LocalSession session, @Arg(desc="The mode to set the trace hook to", def={""}) HookMode hookMode) {
        boolean newMode;
        boolean previousMode = session.isTracingActions();
        if (hookMode != null) {
            boolean bl = newMode = hookMode == HookMode.ACTIVE;
            if (newMode == previousMode) {
                actor.printError(TranslatableComponent.of(previousMode ? "worldedit.trace.active.already" : "worldedit.trace.inactive.already"));
                return;
            }
        } else {
            newMode = !previousMode;
        }
        session.setTracingActions(newMode);
        actor.printInfo(TranslatableComponent.of(newMode ? "worldedit.trace.active" : "worldedit.trace.inactive"));
    }

    @Command(name="cui", desc="Complete CUI handshake (internal usage)")
    public void cui(Player player, LocalSession session) {
        session.setCUISupport(true);
        session.dispatchCUISetup(player);
    }

    @Command(name="tz", desc="Set your timezone for snapshots")
    public void tz(Actor actor, LocalSession session, @Arg(desc="The timezone to set") String timezone) {
        try {
            ZoneId tz = ZoneId.of(timezone);
            session.setTimezone(tz);
            actor.printInfo(TranslatableComponent.of("worldedit.timezone.set", TextComponent.of(tz.getDisplayName(TextStyle.FULL, actor.getLocale()))));
            actor.printInfo(TranslatableComponent.of("worldedit.timezone.current", TextComponent.of(dateFormat.withLocale(actor.getLocale()).format(ZonedDateTime.now(tz)))));
        }
        catch (ZoneRulesException e) {
            actor.printError(TranslatableComponent.of("worldedit.timezone.invalid"));
        }
    }

    @Command(name="help", desc="Displays help for WorldEdit commands")
    @CommandPermissions(value={"worldedit.help"})
    public void help(Actor actor, @Switch(name=115, desc="List sub-commands of the given command, if applicable") boolean listSubCommands, @ArgFlag(name=112, desc="The page to retrieve", def={"1"}) int page, @Arg(desc="The command to retrieve help for", def={""}, variable=true) List<String> command) throws WorldEditException {
        PrintCommandHelp.help(command, page, listSubCommands, this.we.getPlatformManager().getPlatformCommandManager().getCommandManager(), actor, "/worldedit help");
    }
}

