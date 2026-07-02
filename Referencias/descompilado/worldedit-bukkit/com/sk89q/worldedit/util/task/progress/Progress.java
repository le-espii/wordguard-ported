/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.task.progress;

import com.sk89q.worldedit.util.task.progress.ProgressObservable;
import java.util.Arrays;
import java.util.Collection;

public abstract class Progress {
    private static final Progress COMPLETED = Progress.of(1.0);
    private static final Progress INDETERMINATE = new Progress(){

        @Override
        public boolean isIndeterminate() {
            return true;
        }

        @Override
        public double getProgress() {
            return 0.0;
        }
    };

    private Progress() {
    }

    public abstract boolean isIndeterminate();

    public abstract double getProgress();

    public static Progress indeterminate() {
        return INDETERMINATE;
    }

    public static Progress completed() {
        return COMPLETED;
    }

    public static Progress of(double value) {
        if (value < 0.0) {
            value = 0.0;
        } else if (value > 1.0) {
            value = 1.0;
        }
        final double finalValue = value;
        return new Progress(){

            @Override
            public boolean isIndeterminate() {
                return false;
            }

            @Override
            public double getProgress() {
                return finalValue;
            }
        };
    }

    public static Progress split(Progress ... objects) {
        return Progress.split(Arrays.asList(objects));
    }

    public static Progress split(Collection<Progress> progress) {
        boolean count = false;
        double total = 0.0;
        for (Progress p : progress) {
            if (p.isIndeterminate()) {
                return Progress.indeterminate();
            }
            total += p.getProgress();
        }
        return Progress.of(total / (double)count);
    }

    public static Progress splitObservables(ProgressObservable ... observables) {
        return Progress.splitObservables(Arrays.asList(observables));
    }

    public static Progress splitObservables(Collection<? extends ProgressObservable> observables) {
        boolean count = false;
        double total = 0.0;
        for (ProgressObservable progressObservable : observables) {
            Progress p = progressObservable.getProgress();
            if (p.isIndeterminate()) {
                return Progress.indeterminate();
            }
            total += p.getProgress();
        }
        return Progress.of(total / (double)count);
    }
}

