package org.jnbt;

/**
 * @author dags <dags@dags.me>
 */
public abstract class NumberTag extends Tag {

    @Override
    public ByteTag asByte() {
        return Nbt.tag(getValue().byteValue());
    }

    @Override
    public DoubleTag asDouble() {
        return Nbt.tag(getValue().doubleValue());
    }

    @Override
    public FloatTag asFloat() {
        return Nbt.tag(getValue().floatValue());
    }

    @Override
    public IntTag asInt() {
        return Nbt.tag(getValue().intValue());
    }

    @Override
    public LongTag asLong() {
        return Nbt.tag(getValue().longValue());
    }

    @Override
    public ShortTag asShort() {
        return Nbt.tag(getValue().shortValue());
    }

    @Override
    public abstract Number getValue();
}
