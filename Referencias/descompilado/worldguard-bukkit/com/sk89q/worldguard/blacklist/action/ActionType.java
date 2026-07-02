/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldguard.blacklist.action;

import com.sk89q.worldguard.blacklist.Blacklist;
import com.sk89q.worldguard.blacklist.BlacklistEntry;
import com.sk89q.worldguard.blacklist.action.Action;
import com.sk89q.worldguard.blacklist.action.AllowAction;
import com.sk89q.worldguard.blacklist.action.BanAction;
import com.sk89q.worldguard.blacklist.action.DenyAction;
import com.sk89q.worldguard.blacklist.action.KickAction;
import com.sk89q.worldguard.blacklist.action.LogAction;
import com.sk89q.worldguard.blacklist.action.NotifyAction;
import com.sk89q.worldguard.blacklist.action.TellAction;

public enum ActionType {
    ALLOW("allow"){

        @Override
        public Action parseInput(Blacklist blacklist, BlacklistEntry entry) {
            return AllowAction.getInstance();
        }
    }
    ,
    DENY("deny"){

        @Override
        public Action parseInput(Blacklist blacklist, BlacklistEntry entry) {
            return DenyAction.getInstance();
        }
    }
    ,
    BAN("ban"){

        @Override
        public Action parseInput(Blacklist blacklist, BlacklistEntry entry) {
            return new BanAction(entry);
        }
    }
    ,
    KICK("kick"){

        @Override
        public Action parseInput(Blacklist blacklist, BlacklistEntry entry) {
            return new KickAction(entry);
        }
    }
    ,
    LOG("log"){

        @Override
        public Action parseInput(Blacklist blacklist, BlacklistEntry entry) {
            return new LogAction(blacklist, entry);
        }
    }
    ,
    NOTIFY("notify"){

        @Override
        public Action parseInput(Blacklist blacklist, BlacklistEntry entry) {
            return new NotifyAction(blacklist, entry);
        }
    }
    ,
    TELL("tell"){

        @Override
        public Action parseInput(Blacklist blacklist, BlacklistEntry entry) {
            return new TellAction(entry);
        }
    };

    private final String actionName;

    private ActionType(String actionName) {
        this.actionName = actionName;
    }

    public abstract Action parseInput(Blacklist var1, BlacklistEntry var2);

    public String getActionName() {
        return this.actionName;
    }
}

