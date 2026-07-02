/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.ref.Cleaner
 *  java.lang.ref.Cleaner$Cleanable
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.internal.util.ErrorReporting;
import com.sk89q.worldedit.util.eventbus.EventBus;
import com.sk89q.worldedit.world.World;
import java.lang.ref.Cleaner;
import javax.annotation.Nullable;

class TracedEditSession
extends EditSession {
    private static final Cleaner cleaner = Cleaner.create();
    private final TraceRecord record;
    private final Cleaner.Cleanable cleanable;

    TracedEditSession(EventBus eventBus, @Nullable World world, int maxBlocks, @Nullable BlockBag blockBag, @Nullable Actor actor, boolean tracing) {
        super(eventBus, world, maxBlocks, blockBag, actor, tracing);
        this.record = new TraceRecord(actor);
        this.cleanable = cleaner.register((Object)this, (Runnable)this.record);
    }

    @Override
    public void close() {
        try {
            super.close();
        }
        finally {
            this.record.committed = true;
            this.cleanable.clean();
        }
    }

    private static final class TraceRecord
    implements Runnable {
        private final Throwable stacktrace = new Throwable("An EditSession was not closed.");
        private final Actor actor;
        private volatile boolean committed = false;

        private TraceRecord(Actor actor) {
            this.actor = actor;
        }

        @Override
        public void run() {
            if (!this.committed) {
                WorldEdit.logger.warn("####### EDIT SESSION NOT CLOSED #######");
                WorldEdit.logger.warn("This means that some code did not close their EditSession.");
                WorldEdit.logger.warn("Here is a stacktrace from the creation of this EditSession:", this.stacktrace);
                ErrorReporting.trigger(this.actor, this.stacktrace);
            }
        }
    }
}

