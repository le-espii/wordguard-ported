/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 */
package com.sk89q.worldguard.util.command;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class CommandFilter
implements Predicate<String> {
    @Nullable
    private final Collection<String> permitted;
    @Nullable
    private final Collection<String> denied;

    public CommandFilter(@Nullable Collection<String> permitted, @Nullable Collection<String> denied) {
        this.permitted = permitted;
        this.denied = denied;
    }

    public boolean apply(String command) {
        int i;
        command = command.toLowerCase().replaceAll("^/([^ :]*:)?", "/");
        String result = "";
        String[] usedParts = command.split("\\s+");
        if (this.denied != null) {
            block0: for (String deniedCommand : this.denied) {
                String[] deniedParts = deniedCommand.split("\\s+");
                for (i = 0; i < deniedParts.length && i < usedParts.length && deniedParts[i].equalsIgnoreCase(usedParts[i]); ++i) {
                    if (i + 1 == deniedParts.length) {
                        result = deniedCommand;
                        break block0;
                    }
                    if (i + 1 == usedParts.length) continue block0;
                }
            }
        }
        if (this.permitted != null) {
            block2: for (String permittedCommand : this.permitted) {
                String[] permittedParts = permittedCommand.split("\\s+");
                for (i = 0; i < permittedParts.length && i < usedParts.length; ++i) {
                    if (permittedParts[i].equalsIgnoreCase(usedParts[i])) {
                        if (i + 1 == permittedParts.length) {
                            result = "";
                            break block2;
                        }
                        if (i + 1 != usedParts.length) continue;
                        result = command;
                        continue block2;
                    }
                    result = command;
                    continue block2;
                }
            }
        }
        return result.isEmpty();
    }

    public static class Builder {
        private Set<String> permitted;
        private Set<String> denied;

        public Builder permit(String ... rules) {
            Preconditions.checkNotNull((Object)rules);
            if (this.permitted == null) {
                this.permitted = new HashSet<String>();
            }
            this.permitted.addAll(Arrays.asList(rules));
            return this;
        }

        public Builder deny(String ... rules) {
            Preconditions.checkNotNull((Object)rules);
            if (this.denied == null) {
                this.denied = new HashSet<String>();
            }
            this.denied.addAll(Arrays.asList(rules));
            return this;
        }

        public CommandFilter build() {
            return new CommandFilter(this.permitted != null ? new HashSet<String>(this.permitted) : null, (Collection<String>)(this.denied != null ? new HashSet<String>(this.denied) : null));
        }
    }
}

