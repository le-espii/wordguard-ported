/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.piston.suggestion;

import org.enginehub.piston.suggestion.Suggestion;

final class AutoValue_Suggestion
extends Suggestion {
    private final String suggestion;
    private final int replacedArgument;

    private AutoValue_Suggestion(String suggestion, int replacedArgument) {
        this.suggestion = suggestion;
        this.replacedArgument = replacedArgument;
    }

    @Override
    public String getSuggestion() {
        return this.suggestion;
    }

    @Override
    public int getReplacedArgument() {
        return this.replacedArgument;
    }

    public String toString() {
        return "Suggestion{suggestion=" + this.suggestion + ", replacedArgument=" + this.replacedArgument + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Suggestion) {
            Suggestion that = (Suggestion)o;
            return this.suggestion.equals(that.getSuggestion()) && this.replacedArgument == that.getReplacedArgument();
        }
        return false;
    }

    public int hashCode() {
        int h$ = 1;
        h$ *= 1000003;
        h$ ^= this.suggestion.hashCode();
        h$ *= 1000003;
        return h$ ^= this.replacedArgument;
    }

    @Override
    public Suggestion.Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder
    implements Suggestion.Builder {
        private String suggestion;
        private int replacedArgument;
        private byte set$0;

        Builder() {
        }

        private Builder(Suggestion source) {
            this.suggestion = source.getSuggestion();
            this.replacedArgument = source.getReplacedArgument();
            this.set$0 = 1;
        }

        @Override
        public Suggestion.Builder suggestion(String suggestion) {
            if (suggestion == null) {
                throw new NullPointerException("Null suggestion");
            }
            this.suggestion = suggestion;
            return this;
        }

        @Override
        public Suggestion.Builder replacedArgument(int replacedArgument) {
            this.replacedArgument = replacedArgument;
            this.set$0 = (byte)(this.set$0 | 1);
            return this;
        }

        @Override
        public Suggestion build() {
            if (this.set$0 != 1 || this.suggestion == null) {
                StringBuilder missing = new StringBuilder();
                if (this.suggestion == null) {
                    missing.append(" suggestion");
                }
                if ((this.set$0 & 1) == 0) {
                    missing.append(" replacedArgument");
                }
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_Suggestion(this.suggestion, this.replacedArgument);
        }
    }
}

