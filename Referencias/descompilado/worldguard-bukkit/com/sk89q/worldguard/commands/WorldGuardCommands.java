/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.Files
 *  com.google.common.util.concurrent.FutureCallback
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.sk89q.minecraft.util.commands.Command
 *  com.sk89q.minecraft.util.commands.CommandContext
 *  com.sk89q.minecraft.util.commands.CommandException
 *  com.sk89q.minecraft.util.commands.CommandPermissions
 *  com.sk89q.minecraft.util.commands.NestedCommand
 *  com.sk89q.worldedit.WorldEdit
 *  com.sk89q.worldedit.extension.platform.Actor
 *  com.sk89q.worldedit.extension.platform.Capability
 *  com.sk89q.worldedit.util.auth.AuthorizationException
 *  com.sk89q.worldedit.util.formatting.component.MessageBox
 *  com.sk89q.worldedit.util.formatting.component.TextComponentProducer
 *  com.sk89q.worldedit.util.formatting.text.Component
 *  com.sk89q.worldedit.util.formatting.text.TextComponent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent
 *  com.sk89q.worldedit.util.formatting.text.event.ClickEvent$Action
 *  com.sk89q.worldedit.util.formatting.text.format.TextColor
 *  com.sk89q.worldedit.util.paste.ActorCallbackPaste
 *  com.sk89q.worldedit.util.report.Report
 *  com.sk89q.worldedit.util.report.ReportList
 *  com.sk89q.worldedit.util.report.SystemInfoReport
 *  com.sk89q.worldedit.util.task.FutureForwardingTask
 *  com.sk89q.worldedit.util.task.Supervisor
 *  com.sk89q.worldedit.util.task.Task
 *  com.sk89q.worldedit.util.task.TaskStateComparator
 *  com.sk89q.worldedit.world.World
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.commands;

import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.component.MessageBox;
import com.sk89q.worldedit.util.formatting.component.TextComponentProducer;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.event.ClickEvent;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import com.sk89q.worldedit.util.paste.ActorCallbackPaste;
import com.sk89q.worldedit.util.report.Report;
import com.sk89q.worldedit.util.report.ReportList;
import com.sk89q.worldedit.util.report.SystemInfoReport;
import com.sk89q.worldedit.util.task.FutureForwardingTask;
import com.sk89q.worldedit.util.task.Supervisor;
import com.sk89q.worldedit.util.task.Task;
import com.sk89q.worldedit.util.task.TaskStateComparator;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.commands.DebuggingCommands;
import com.sk89q.worldguard.config.ConfigurationManager;
import com.sk89q.worldguard.util.logging.LoggerToChatHandler;
import com.sk89q.worldguard.util.profiler.SamplerBuilder;
import com.sk89q.worldguard.util.profiler.ThreadIdFilter;
import com.sk89q.worldguard.util.profiler.ThreadNameFilter;
import com.sk89q.worldguard.util.report.ApplicableRegionsReport;
import com.sk89q.worldguard.util.report.ConfigReport;
import java.io.File;
import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

public class WorldGuardCommands {
    private final WorldGuard worldGuard;
    @Nullable
    private SamplerBuilder.Sampler activeSampler;

    public WorldGuardCommands(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    @Command(aliases={"version"}, desc="Get the WorldGuard version", max=0)
    public void version(CommandContext args, Actor sender) throws CommandException {
        sender.print("WorldGuard " + WorldGuard.getVersion());
        sender.print("http://www.enginehub.org");
        sender.printDebug("----------- Platforms -----------");
        sender.printDebug(String.format("* %s (%s)", this.worldGuard.getPlatform().getPlatformName(), this.worldGuard.getPlatform().getPlatformVersion()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Command(aliases={"reload"}, desc="Reload WorldGuard configuration", max=0)
    @CommandPermissions(value={"worldguard.reload"})
    public void reload(CommandContext args, Actor sender) throws CommandException {
        List tasks = WorldGuard.getInstance().getSupervisor().getTasks();
        if (!tasks.isEmpty()) {
            throw new CommandException("There are currently pending tasks. Use /wg running to monitor these tasks first.");
        }
        LoggerToChatHandler handler = null;
        Logger minecraftLogger = null;
        if (sender instanceof LocalPlayer) {
            handler = new LoggerToChatHandler(sender);
            handler.setLevel(Level.ALL);
            minecraftLogger = Logger.getLogger("com.sk89q.worldguard");
            minecraftLogger.addHandler(handler);
        }
        try {
            ConfigurationManager config = WorldGuard.getInstance().getPlatform().getGlobalStateManager();
            config.unload();
            config.load();
            for (World world : WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getWorlds()) {
                config.get(world);
            }
            WorldGuard.getInstance().getPlatform().getRegionContainer().reload();
            sender.print("WorldGuard configuration reloaded.");
        }
        catch (Throwable t) {
            sender.printError("Error while reloading: " + t.getMessage());
        }
        finally {
            if (minecraftLogger != null) {
                minecraftLogger.removeHandler(handler);
            }
        }
    }

    @Command(aliases={"report"}, desc="Writes a report on WorldGuard", flags="p", max=0)
    @CommandPermissions(value={"worldguard.report"})
    public void report(CommandContext args, Actor sender) throws CommandException, AuthorizationException {
        ReportList report = new ReportList("Report");
        this.worldGuard.getPlatform().addPlatformReports(report);
        report.add((Report)new SystemInfoReport());
        report.add((Report)new ConfigReport());
        if (sender instanceof LocalPlayer) {
            report.add((Report)new ApplicableRegionsReport((LocalPlayer)sender));
        }
        String result = report.toString();
        try {
            File dest = new File(this.worldGuard.getPlatform().getConfigDir().toFile(), "report.txt");
            Files.write((CharSequence)result, (File)dest, (Charset)StandardCharsets.UTF_8);
            sender.print("WorldGuard report written to " + dest.getAbsolutePath());
        }
        catch (IOException e) {
            throw new CommandException("Failed to write report: " + e.getMessage());
        }
        if (args.hasFlag('p')) {
            sender.checkPermission("worldguard.report.pastebin");
            ActorCallbackPaste.pastebin((Supervisor)this.worldGuard.getSupervisor(), (Actor)sender, (String)result, (String)"WorldGuard report: %s.report");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Command(aliases={"profile"}, usage="[-p] [-i <interval>] [-t <thread filter>] [<minutes>]", desc="Profile the CPU usage of the server", min=0, max=1, flags="t:i:p")
    @CommandPermissions(value={"worldguard.profile"})
    public void profile(CommandContext args, final Actor sender) throws CommandException, AuthorizationException {
        SamplerBuilder.Sampler sampler;
        int minutes;
        boolean pastebin;
        String threadName = args.getFlag('t');
        if (args.hasFlag('p')) {
            sender.checkPermission("worldguard.report.pastebin");
            pastebin = true;
        } else {
            pastebin = false;
        }
        Predicate<ThreadInfo> threadFilter = threadName == null ? new ThreadIdFilter(Thread.currentThread().getId()) : (threadName.equals("*") ? thread -> true : new ThreadNameFilter(threadName));
        if (args.argsLength() == 0) {
            minutes = 5;
        } else {
            minutes = args.getInteger(0);
            if (minutes < 1) {
                throw new CommandException("You must run the profile for at least 1 minute.");
            }
            if (minutes > 10) {
                throw new CommandException("You can profile for, at maximum, 10 minutes.");
            }
        }
        int interval = 20;
        if (args.hasFlag('i')) {
            interval = args.getFlagInteger('i');
            if (interval < 1 || interval > 100) {
                throw new CommandException("Interval must be between 1 and 100 (in milliseconds)");
            }
            if (interval < 10) {
                sender.printDebug("Note: A low interval may cause additional slowdown during profiling.");
            }
        }
        WorldGuardCommands worldGuardCommands = this;
        synchronized (worldGuardCommands) {
            if (this.activeSampler != null) {
                throw new CommandException("A profile is currently in progress! Please use /wg stopprofile to cancel the current profile.");
            }
            SamplerBuilder builder = new SamplerBuilder();
            builder.setThreadFilter(threadFilter);
            builder.setRunTime(minutes, TimeUnit.MINUTES);
            builder.setInterval(interval);
            sampler = this.activeSampler = builder.start();
        }
        sender.print(((TextComponent)((TextComponent)((TextComponent)TextComponent.of((String)("Starting CPU profiling. Results will be available in " + minutes + " minutes."), (TextColor)TextColor.LIGHT_PURPLE).append((Component)TextComponent.newline())).append((Component)TextComponent.of((String)"Use ", (TextColor)TextColor.GRAY))).append(TextComponent.of((String)"/wg stopprofile", (TextColor)TextColor.AQUA).clickEvent(ClickEvent.of((ClickEvent.Action)ClickEvent.Action.SUGGEST_COMMAND, (String)"/wg stopprofile")))).append((Component)TextComponent.of((String)" at any time to cancel CPU profiling.", (TextColor)TextColor.GRAY)));
        this.worldGuard.getSupervisor().monitor((Task)FutureForwardingTask.create(sampler.getFuture(), (String)("CPU profiling for " + minutes + " minutes"), (Object)sender));
        sampler.getFuture().addListener(() -> {
            WorldGuardCommands worldGuardCommands = this;
            synchronized (worldGuardCommands) {
                this.activeSampler = null;
            }
        }, MoreExecutors.directExecutor());
        Futures.addCallback(sampler.getFuture(), (FutureCallback)new FutureCallback<SamplerBuilder.Sampler>(){

            public void onSuccess(SamplerBuilder.Sampler result) {
                String output = result.toString();
                try {
                    File dest = new File(WorldGuardCommands.this.worldGuard.getPlatform().getConfigDir().toFile(), "profile.txt");
                    Files.write((CharSequence)output, (File)dest, (Charset)StandardCharsets.UTF_8);
                    sender.print("CPU profiling data written to " + dest.getAbsolutePath());
                }
                catch (IOException e) {
                    sender.printError("Failed to write CPU profiling data: " + e.getMessage());
                }
                if (pastebin) {
                    ActorCallbackPaste.pastebin((Supervisor)WorldGuardCommands.this.worldGuard.getSupervisor(), (Actor)sender, (String)output, (String)"Profile result: %s.profile");
                }
            }

            public void onFailure(Throwable throwable) {
            }
        }, (Executor)MoreExecutors.directExecutor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Command(aliases={"stopprofile"}, usage="", desc="Stop a running profile", min=0, max=0)
    @CommandPermissions(value={"worldguard.profile"})
    public void stopProfile(CommandContext args, Actor sender) throws CommandException {
        WorldGuardCommands worldGuardCommands = this;
        synchronized (worldGuardCommands) {
            if (this.activeSampler == null) {
                throw new CommandException("No CPU profile is currently running.");
            }
            this.activeSampler.cancel();
            this.activeSampler = null;
        }
        sender.print("The running CPU profile has been cancelled.");
    }

    @Command(aliases={"flushstates", "clearstates"}, usage="[player]", desc="Flush the state manager", max=1)
    @CommandPermissions(value={"worldguard.flushstates"})
    public void flushStates(CommandContext args, Actor sender) throws CommandException {
        if (args.argsLength() == 0) {
            WorldGuard.getInstance().getPlatform().getSessionManager().resetAllStates();
            sender.print("Cleared all states.");
        } else {
            LocalPlayer player = this.worldGuard.getPlatform().getMatcher().matchSinglePlayer(sender, args.getString(0));
            if (player != null) {
                WorldGuard.getInstance().getPlatform().getSessionManager().resetState(player);
                sender.print("Cleared states for player \"" + player.getName() + "\".");
            }
        }
    }

    @Command(aliases={"running", "queue"}, desc="List running tasks", max=0)
    @CommandPermissions(value={"worldguard.running"})
    public void listRunningTasks(CommandContext args, Actor sender) throws CommandException {
        List tasks = WorldGuard.getInstance().getSupervisor().getTasks();
        if (tasks.isEmpty()) {
            sender.print("There are currently no running tasks.");
        } else {
            tasks.sort(new TaskStateComparator());
            MessageBox builder = new MessageBox("Running Tasks", new TextComponentProducer());
            builder.append((Component)TextComponent.of((String)"Note: Some 'running' tasks may be waiting to be start.", (TextColor)TextColor.GRAY));
            for (Task task : tasks) {
                builder.append((Component)TextComponent.newline());
                builder.append((Component)TextComponent.of((String)("(" + task.getState().name() + ") "), (TextColor)TextColor.BLUE));
                builder.append((Component)TextComponent.of((String)(CommandUtils.getOwnerName(task.getOwner()) + ": "), (TextColor)TextColor.YELLOW));
                builder.append((Component)TextComponent.of((String)task.getName(), (TextColor)TextColor.WHITE));
            }
            sender.print((Component)builder.create());
        }
    }

    @Command(aliases={"debug"}, desc="Debugging commands")
    @NestedCommand(value={DebuggingCommands.class})
    public void debug(CommandContext args, Actor sender) {
    }
}

