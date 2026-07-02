/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.auto.value.AutoValue
 *  com.google.auto.value.AutoValue$Builder
 */
package org.enginehub.piston.suggestion;

import com.google.auto.value.AutoValue;
import org.enginehub.piston.suggestion.AutoValue_Suggestion;

@AutoValue
public abstract class Suggestion {
    public static Builder builder() {
        return new AutoValue_Suggestion.Builder();
    }

    Suggestion() {
    }

    public abstract String getSuggestion();

    public abstract int getReplacedArgument();

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static interface Builder {
        public Builder suggestion(String var1);

        public Builder replacedArgument(int var1);

        public Suggestion build();
    }
}

