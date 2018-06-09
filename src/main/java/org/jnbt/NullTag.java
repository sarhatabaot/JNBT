package org.jnbt;

import java.io.DataOutput;

class NullTag extends Tag<Object> {

    static final NullTag NULL = new NullTag();

    @Override
    TagType<Object, NullTag> getType() {
        return TagType.NULL;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    protected Object getValue() {
        return "null";
    }

    @Override
    void writeValue(DataOutput out) {

    }

    @SuppressWarnings("unchecked")
    static <T> Tag<T> empty() {
        return (Tag<T>) NULL;
    }
}
