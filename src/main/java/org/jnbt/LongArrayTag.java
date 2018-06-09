package org.jnbt;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class LongArrayTag extends Tag<long[]> {

    static final LongArrayTag EMPTY = new LongArrayTag(new long[0]);

    private final long[] value;

    LongArrayTag(long[] value) {
        this.value = value;
    }

    LongArrayTag(long[] value, TagType type) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return this != EMPTY;
    }

    @Override
    public LongArrayTag asLongArray() {
        return this;
    }

    @Override
    public long[] getValue() {
        return value;
    }

    @Override
    String getValueString() {
        return Arrays.toString(value);
    }

    @Override
    TagType<long[], LongArrayTag> getType() {
        return TagType.LONG_ARRAY;
    }

    @Override
    void writeValue(DataOutput out) throws IOException {
        out.writeInt(value.length);
        for (long l : value) {
            out.writeLong(l);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LongArrayTag)) return false;
        if (!super.equals(obj)) return false;
        LongArrayTag longArrayTag = (LongArrayTag) obj;
        return Arrays.equals(value, longArrayTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    public static LongArrayTag of(long[] longs) {
        if (longs == null) {
            return EMPTY;
        }
        return new LongArrayTag(longs);
    }
}
