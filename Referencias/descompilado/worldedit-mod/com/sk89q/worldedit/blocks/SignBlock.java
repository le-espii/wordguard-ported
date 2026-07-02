/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.blocks;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.LegacyBaseBlockWrapper;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.util.gson.GsonUtil;
import com.sk89q.worldedit.world.block.BlockState;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

@Deprecated
public class SignBlock
extends LegacyBaseBlockWrapper {
    private String[] text;
    private static final String EMPTY = "{\"text\":\"\"}";

    public SignBlock(BlockState blockState, String[] text) {
        super(blockState);
        if (text == null) {
            this.text = new String[]{EMPTY, EMPTY, EMPTY, EMPTY};
            return;
        }
        for (int i = 0; i < text.length; ++i) {
            text[i] = text[i].isEmpty() ? EMPTY : "{\"text\":" + GsonUtil.stringValue(text[i]) + "}";
        }
        this.text = text;
    }

    private boolean isLegacy() {
        int dataVersion = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.WORLD_EDITING).getDataVersion();
        return dataVersion < 3463;
    }

    public String[] getText() {
        return this.text;
    }

    public void setText(String[] text) {
        if (text == null) {
            throw new IllegalArgumentException("Can't set null text for a sign");
        }
        this.text = text;
    }

    @Override
    @Deprecated
    public boolean hasNbtData() {
        return true;
    }

    @Override
    public String getNbtId() {
        return "minecraft:sign";
    }

    @Override
    @Deprecated
    public CompoundTag getNbtData() {
        HashMap values = new HashMap();
        if (this.isLegacy()) {
            values.put("Text1", new StringTag(this.text[0]));
            values.put("Text2", new StringTag(this.text[1]));
            values.put("Text3", new StringTag(this.text[2]));
            values.put("Text4", new StringTag(this.text[3]));
        } else {
            ListTag messages = new ListTag(StringTag.class, Arrays.stream(this.text).map(StringTag::new).collect(Collectors.toList()));
            HashMap frontTextTag = new HashMap();
            frontTextTag.put("messages", messages);
            values.put("front_text", new CompoundTag(frontTextTag));
        }
        return new CompoundTag(values);
    }

    @Override
    @Deprecated
    public void setNbtData(CompoundTag rootTag) {
        if (rootTag == null) {
            return;
        }
        Object values = rootTag.getValue();
        this.text = new String[]{EMPTY, EMPTY, EMPTY, EMPTY};
        Tag t = (Tag)values.get("id");
        if (!(t instanceof StringTag) || !((StringTag)t).getValue().equals(this.getNbtId())) {
            throw new RuntimeException(String.format("'%s' tile entity expected", this.getNbtId()));
        }
        if (this.isLegacy()) {
            t = (Tag)values.get("Text1");
            if (t instanceof StringTag) {
                this.text[0] = ((StringTag)t).getValue();
            }
            if ((t = (Tag)values.get("Text2")) instanceof StringTag) {
                this.text[1] = ((StringTag)t).getValue();
            }
            if ((t = (Tag)values.get("Text3")) instanceof StringTag) {
                this.text[2] = ((StringTag)t).getValue();
            }
            if ((t = (Tag)values.get("Text4")) instanceof StringTag) {
                this.text[3] = ((StringTag)t).getValue();
            }
        } else {
            CompoundTag frontTextTag = (CompoundTag)values.get("front_text");
            ListTag messagesTag = frontTextTag.getListTag("messages");
            for (int i = 0; i < messagesTag.getValue().size(); ++i) {
                StringTag tag = (StringTag)messagesTag.getValue().get(i);
                this.text[i] = tag.getValue();
            }
        }
    }
}

