package org.jnbt;

import java.io.DataOutput;

class NullTag extends Tag {

    static final NullTag NULL = new NullTag();

    @Override
    public TagType getType() {
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
}
