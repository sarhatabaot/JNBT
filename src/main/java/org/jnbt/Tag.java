package org.jnbt;

/*
 * JNBT License
 *
 * Copyright (c) 2010 Graham Edgecombe
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     * Neither the name of the JNBT team nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public abstract class Tag<V> {

    public boolean isAbsent() {
        return !isPresent();
    }

    String getValueString() {
        if (isAbsent()) {
            return "null";
        }
        return getValue().toString();
    }

    public ByteArrayTag asByteArray() {
        return ByteArrayTag.EMPTY;
    }

    public ByteTag asByte() {
        return ByteTag.EMPTY;
    }

    public CompoundTag asCompound() {
        return CompoundTag.EMPTY;
    }

    public <S, T extends Tag<S>> ListTag<S> asList(TagType<S, T> type) {
        return ListTag.empty();
    }

    public DoubleTag asDouble() {
        return DoubleTag.EMPTY;
    }

    public FloatTag asFloat() {
        return FloatTag.EMPTY;
    }

    public IntArrayTag asIntArray() {
        return IntArrayTag.EMPTY;
    }

    public IntTag asInt() {
        return IntTag.EMPTY;
    }

    public LongArrayTag asLongArray() {
        return LongArrayTag.EMPTY;
    }

    public LongTag asLong() {
        return LongTag.EMPTY;
    }

    public ShortTag asShort() {
        return ShortTag.EMPTY;
    }

    public StringTag asString() {
        if (isAbsent()) {
            return StringTag.EMPTY;
        }
        if (getType() == TagType.COMPOUND || getType() == TagType.LIST) {
            return StringTag.EMPTY;
        }
        return Nbt.tag("" + getValue());
    }

    abstract TagType<V, ?> getType();

    public abstract boolean isPresent();

    public abstract V getValue();

    abstract void writeValue(DataOutput out) throws IOException;

    void writeTo(String name, DataOutput out) throws IOException {
        out.writeByte(getType().getId());

        byte[] nameBytes = name.getBytes(StringTag.CHARSET);
        out.writeShort(nameBytes.length);
        out.write(nameBytes);
        writeValue(out);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return getType() == tag.getType() && Objects.equals(getValue(), tag.getValue());
    }

    @Override
    public String toString() {
        return getType().getName() + "(" + getValueString() + ")";
    }
}
