/*
 * Decompiled with CFR 0.152.
 */
package org.enginehub.linbus.stream;

public final class LinReadOptions {
    private final boolean allowNormalUtf8Encoding;

    public static Builder builder() {
        return new Builder();
    }

    private LinReadOptions(Builder builder) {
        this.allowNormalUtf8Encoding = builder.allowNormalUtf8Encoding;
    }

    public boolean allowNormalUtf8Encoding() {
        return this.allowNormalUtf8Encoding;
    }

    public String toString() {
        return "LinReadOptions{allowNormalUtf8Encoding=" + this.allowNormalUtf8Encoding + "}";
    }

    public static final class Builder {
        private boolean allowNormalUtf8Encoding = false;

        private Builder() {
        }

        public Builder allowNormalUtf8Encoding(boolean allowNormalUtf8Encoding) {
            this.allowNormalUtf8Encoding = allowNormalUtf8Encoding;
            return this;
        }

        public LinReadOptions build() {
            return new LinReadOptions(this);
        }

        public String toString() {
            return "LinReadOptions.Builder{allowNormalUtf8Encoding=" + this.allowNormalUtf8Encoding + "}";
        }
    }
}

