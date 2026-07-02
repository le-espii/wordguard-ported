/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.internal.expression;

public interface LocalSlot {
    public double value();

    public static final class Variable
    implements LocalSlot {
        private double value;

        public Variable(double value) {
            this.value = value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public double value() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }
    }

    public record Constant(double value) implements LocalSlot
    {
        public String toString() {
            return String.valueOf(this.value);
        }
    }
}

