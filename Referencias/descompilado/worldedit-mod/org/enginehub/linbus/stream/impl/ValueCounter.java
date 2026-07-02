/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.runtime.SwitchBootstraps
 */
package org.enginehub.linbus.stream.impl;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import org.enginehub.linbus.stream.exception.NbtParseException;
import org.enginehub.linbus.stream.token.LinToken;

public class ValueCounter {
    private static final byte BYTE_ARRAY = 1;
    private static final byte INT_ARRAY = 2;
    private static final byte LONG_ARRAY = 3;
    private int count;
    private int compounds;
    private int lists;
    private byte arrayType;

    public void add(LinToken token) {
        if (token.isSimpleValue() && !this.isNested()) {
            ++this.count;
            return;
        }
        LinToken linToken = token;
        Objects.requireNonNull(linToken);
        LinToken linToken2 = linToken;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{LinToken.CompoundStart.class, LinToken.CompoundEnd.class, LinToken.ListStart.class, LinToken.ListEnd.class, LinToken.ByteArrayStart.class, LinToken.ByteArrayEnd.class, LinToken.IntArrayStart.class, LinToken.IntArrayEnd.class, LinToken.LongArrayStart.class, LinToken.LongArrayEnd.class}, (Object)linToken2, (int)n)) {
            case 0: {
                LinToken.CompoundStart compoundStart = (LinToken.CompoundStart)linToken2;
                ++this.compounds;
                break;
            }
            case 1: {
                LinToken.CompoundEnd compoundEnd = (LinToken.CompoundEnd)linToken2;
                --this.compounds;
                if (this.compounds < 0) {
                    throw new NbtParseException("Compound end without start");
                }
                if (this.isNested()) break;
                ++this.count;
                break;
            }
            case 2: {
                LinToken.ListStart listStart = (LinToken.ListStart)linToken2;
                ++this.lists;
                break;
            }
            case 3: {
                LinToken.ListEnd listEnd = (LinToken.ListEnd)linToken2;
                --this.lists;
                if (this.lists < 0) {
                    throw new NbtParseException("List end without start");
                }
                if (this.isNested()) break;
                ++this.count;
                break;
            }
            case 4: {
                LinToken.ByteArrayStart byteArrayStart = (LinToken.ByteArrayStart)linToken2;
                this.arrayType = 1;
                break;
            }
            case 5: {
                LinToken.ByteArrayEnd byteArrayEnd = (LinToken.ByteArrayEnd)linToken2;
                if (this.arrayType != 1) {
                    throw new NbtParseException("Byte array end without start");
                }
                this.arrayType = 0;
                if (this.isNested()) break;
                ++this.count;
                break;
            }
            case 6: {
                LinToken.IntArrayStart intArrayStart = (LinToken.IntArrayStart)linToken2;
                this.arrayType = (byte)2;
                break;
            }
            case 7: {
                LinToken.IntArrayEnd intArrayEnd = (LinToken.IntArrayEnd)linToken2;
                if (this.arrayType != 2) {
                    throw new NbtParseException("Int array end without start");
                }
                this.arrayType = 0;
                if (this.isNested()) break;
                ++this.count;
                break;
            }
            case 8: {
                LinToken.LongArrayStart longArrayStart = (LinToken.LongArrayStart)linToken2;
                this.arrayType = (byte)3;
                break;
            }
            case 9: {
                LinToken.LongArrayEnd longArrayEnd = (LinToken.LongArrayEnd)linToken2;
                if (this.arrayType != 3) {
                    throw new NbtParseException("Long array end without start");
                }
                this.arrayType = 0;
                if (this.isNested()) break;
                ++this.count;
                break;
            }
        }
    }

    public int count() {
        return this.count;
    }

    public boolean isNested() {
        return this.lists > 0 || this.compounds > 0;
    }
}

