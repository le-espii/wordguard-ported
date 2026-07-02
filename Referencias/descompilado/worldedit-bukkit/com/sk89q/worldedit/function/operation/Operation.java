/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.function.operation;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface Operation {
    public static final Set<String> warnedDeprecatedClasses = new HashSet<String>();

    public Operation resume(RunContext var1) throws WorldEditException;

    public void cancel();

    @Deprecated
    default public void addStatusMessages(List<String> messages) {
    }

    default public Iterable<Component> getStatusMessages() {
        String className;
        ArrayList<String> oldMessages = new ArrayList<String>();
        this.addStatusMessages(oldMessages);
        if (oldMessages.size() > 0 && !warnedDeprecatedClasses.contains(className = this.getClass().getName())) {
            WorldEdit.logger.warn("An operation is using the old status message API. This will be removed in WorldEdit 8. Class: " + className);
            warnedDeprecatedClasses.add(className);
        }
        return oldMessages.stream().map(TextComponent::of).collect(Collectors.toList());
    }
}

