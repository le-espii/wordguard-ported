/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.sk89q.worldedit.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class TreeGenerator {
    private static final Random RANDOM = new Random();

    private TreeGenerator() {
    }

    private static void makePineTree(EditSession editSession, BlockVector3 basePosition) throws MaxChangedBlocksException {
        int i;
        int trunkHeight = (int)Math.floor(Math.random() * 2.0) + 3;
        int height = (int)Math.floor(Math.random() * 5.0) + 8;
        BlockState logBlock = BlockTypes.OAK_LOG.getDefaultState();
        BlockState leavesBlock = BlockTypes.OAK_LEAVES.getDefaultState();
        for (i = 0; i < trunkHeight; ++i) {
            if (TreeGenerator.setBlockIfAir(editSession, basePosition.add(0, i, 0), logBlock)) continue;
            return;
        }
        basePosition = basePosition.add(0, trunkHeight, 0);
        for (i = 0; i < height; ++i) {
            int j;
            TreeGenerator.setBlockIfAir(editSession, basePosition.add(0, i, 0), logBlock);
            double chance = i == 0 || i == height - 1 ? 0.6 : 1.0;
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(-1, i, 0), leavesBlock, chance);
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(1, i, 0), leavesBlock, chance);
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(0, i, -1), leavesBlock, chance);
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(0, i, 1), leavesBlock, chance);
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(1, i, 1), leavesBlock, chance);
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(-1, i, 1), leavesBlock, chance);
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(1, i, -1), leavesBlock, chance);
            TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(-1, i, -1), leavesBlock, chance);
            if (i == 0 || i == height - 1) continue;
            for (j = -2; j <= 2; ++j) {
                TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(-2, i, j), leavesBlock, 0.6);
            }
            for (j = -2; j <= 2; ++j) {
                TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(2, i, j), leavesBlock, 0.6);
            }
            for (j = -2; j <= 2; ++j) {
                TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(j, i, -2), leavesBlock, 0.6);
            }
            for (j = -2; j <= 2; ++j) {
                TreeGenerator.setChanceBlockIfAir(editSession, basePosition.add(j, i, 2), leavesBlock, 0.6);
            }
        }
        TreeGenerator.setBlockIfAir(editSession, basePosition.add(0, height, 0), leavesBlock);
    }

    @Nullable
    public static TreeType lookup(String type) {
        return TreeType.lookup(type);
    }

    private static <B extends BlockStateHolder<B>> boolean setChanceBlockIfAir(EditSession session, BlockVector3 position, B block, double probability) throws MaxChangedBlocksException {
        return Math.random() <= probability && TreeGenerator.setBlockIfAir(session, position, block);
    }

    private static <B extends BlockStateHolder<B>> boolean setBlockIfAir(EditSession session, BlockVector3 position, B block) throws MaxChangedBlocksException {
        return session.getBlock(position).getBlockType().getMaterial().isAir() && session.setBlock(position, block);
    }

    public static enum TreeType {
        TREE("Oak tree", "oak", "tree", "regular"),
        BIG_TREE("Large oak tree", "largeoak", "bigoak", "big", "bigtree"),
        REDWOOD("Spruce tree", "spruce", "redwood", "sequoia", "sequoioideae"),
        TALL_REDWOOD("Tall spruce tree", "tallspruce", "bigspruce", "tallredwood", "tallsequoia", "tallsequoioideae"),
        MEGA_REDWOOD("Large spruce tree", "largespruce", "megaredwood"),
        RANDOM_REDWOOD("Random spruce tree", new String[]{"randspruce", "randredwood", "randomredwood", "anyredwood"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                TreeType[] choices = new TreeType[]{REDWOOD, TALL_REDWOOD, MEGA_REDWOOD};
                return choices[TreeGenerator.RANDOM.nextInt(choices.length)].generate(editSession, pos);
            }
        }
        ,
        BIRCH("Birch tree", "birch", "white", "whitebark"),
        TALL_BIRCH("Tall birch tree", "tallbirch"),
        RANDOM_BIRCH("Random birch tree", new String[]{"randbirch", "randombirch"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                TreeType[] choices = new TreeType[]{BIRCH, TALL_BIRCH};
                return choices[TreeGenerator.RANDOM.nextInt(choices.length)].generate(editSession, pos);
            }
        }
        ,
        JUNGLE("Jungle tree", "jungle"),
        SMALL_JUNGLE("Small jungle tree", "smalljungle"),
        SHORT_JUNGLE("Short jungle tree", new String[]{"shortjungle"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                return SMALL_JUNGLE.generate(editSession, pos);
            }
        }
        ,
        RANDOM_JUNGLE("Random jungle tree", new String[]{"randjungle", "randomjungle"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                TreeType[] choices = new TreeType[]{JUNGLE, SMALL_JUNGLE};
                return choices[TreeGenerator.RANDOM.nextInt(choices.length)].generate(editSession, pos);
            }
        }
        ,
        JUNGLE_BUSH("Jungle bush", "junglebush", "jungleshrub"),
        RED_MUSHROOM("Red mushroom", "redmushroom", "redgiantmushroom"),
        BROWN_MUSHROOM("Brown mushroom", "brownmushroom", "browngiantmushroom"),
        CRIMSON_FUNGUS("Crimson fungus", "crimsonfungus", "rednethermushroom"),
        WARPED_FUNGUS("Warped fungus", "warpedfungus", "greennethermushroom"),
        RANDOM_MUSHROOM("Random mushroom", new String[]{"randmushroom", "randommushroom"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                TreeType[] choices = new TreeType[]{RED_MUSHROOM, BROWN_MUSHROOM, CRIMSON_FUNGUS, WARPED_FUNGUS};
                return choices[TreeGenerator.RANDOM.nextInt(choices.length)].generate(editSession, pos);
            }
        }
        ,
        SWAMP("Swamp tree", "swamp", "swamptree"),
        ACACIA("Acacia tree", "acacia"),
        DARK_OAK("Dark oak tree", "darkoak"),
        PINE("Pine tree", new String[]{"pine"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                TreeGenerator.makePineTree(editSession, pos);
                return true;
            }
        }
        ,
        CHORUS_PLANT("Chorus plant", new String[]{"chorusplant"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                return editSession.getWorld().generateTree(this, editSession, pos.subtract(0, 1, 0));
            }
        }
        ,
        MANGROVE("Mangrove tree", "mangrove"),
        TALL_MANGROVE("Tall mangrove tree", "tall_mangrove"),
        CHERRY("Cherry blossom", "cherry"),
        RANDOM("Random tree", new String[]{"rand", "random"}){

            @Override
            public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
                TreeType[] choices = TreeType.values();
                return choices[TreeGenerator.RANDOM.nextInt(choices.length)].generate(editSession, pos);
            }
        };

        private static final Map<String, TreeType> lookup;
        private static final Set<String> primaryAliases;
        private final String name;
        public final ImmutableList<String> lookupKeys;

        private TreeType(String name, String ... lookupKeys) {
            this.name = name;
            this.lookupKeys = ImmutableList.copyOf((Object[])lookupKeys);
        }

        public static Set<String> getAliases() {
            return Collections.unmodifiableSet(lookup.keySet());
        }

        public static Set<String> getPrimaryAliases() {
            return Collections.unmodifiableSet(primaryAliases);
        }

        public boolean generate(EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
            return editSession.getWorld().generateTree(this, editSession, pos);
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static TreeType lookup(String name) {
            return lookup.get(name.toLowerCase(Locale.ROOT));
        }

        static {
            lookup = new HashMap<String, TreeType>();
            primaryAliases = Sets.newHashSet();
            for (TreeType type : EnumSet.allOf(TreeType.class)) {
                for (String key : type.lookupKeys) {
                    lookup.put(key, type);
                }
                if (type.lookupKeys.size() <= 0) continue;
                primaryAliases.add((String)type.lookupKeys.get(0));
            }
        }
    }
}

