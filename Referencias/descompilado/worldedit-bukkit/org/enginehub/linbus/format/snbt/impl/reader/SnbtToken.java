/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.format.snbt.impl.reader;

public sealed interface SnbtToken {

    public record Text(boolean quoted, String content) implements SnbtToken
    {
    }

    public static enum Separator implements SnbtToken
    {
        INSTANCE;


        public String toString() {
            return "','";
        }
    }

    public static enum ListTypeSeparator implements SnbtToken
    {
        INSTANCE;


        public String toString() {
            return "';'";
        }
    }

    public static enum EntrySeparator implements SnbtToken
    {
        INSTANCE;


        public String toString() {
            return "':'";
        }
    }

    public static enum ListLikeEnd implements SnbtToken
    {
        INSTANCE;


        public String toString() {
            return "']'";
        }
    }

    public static enum ListLikeStart implements SnbtToken
    {
        INSTANCE;


        public String toString() {
            return "'['";
        }
    }

    public static enum CompoundEnd implements SnbtToken
    {
        INSTANCE;


        public String toString() {
            return "'}'";
        }
    }

    public static enum CompoundStart implements SnbtToken
    {
        INSTANCE;


        public String toString() {
            return "'{'";
        }
    }
}

