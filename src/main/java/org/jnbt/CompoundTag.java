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

import com.sun.javafx.collections.UnmodifiableObservableMap;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CompoundTag extends Tag implements Iterable<Map.Entry<String, Tag>> {

    static final CompoundTag EMPTY = new CompoundTag(Collections.emptyMap());

    private final Map<String, Tag> value;

    CompoundTag(Map<String, Tag> value) {
        this.value = value;
    }

    public CompoundTag copy() {
        return new CompoundTag(new LinkedHashMap<>(value));
    }

    public CompoundTag immutable() {
        if (value instanceof UnmodifiableObservableMap) {
            return this;
        }
        return new CompoundTag(Collections.unmodifiableMap(value));
    }

    public CompoundTag immutableCopy() {
        return new CompoundTag(Collections.unmodifiableMap(new LinkedHashMap<>(value)));
    }

    @Override
    public boolean isPresent() {
        return this != EMPTY;
    }

    @Override
    public CompoundTag asCompound() {
        return this;
    }

    @Override
    public Map<String, Tag> getValue() {
        return value;
    }

    @Override
    public TagType getType() {
        return TagType.COMPOUND;
    }

    @Override
    void writeValue(DataOutput out) throws IOException {
        for (Map.Entry<String, Tag> entry : this) {
            entry.getValue().writeTo(entry.getKey(), out);
        }
        out.writeByte(TagType.END.getId());
    }

    public CompoundTag put(String key, Tag tag) {
        if (tag.isPresent()) {
            value.put(key, tag);
        }
        return this;
    }

    public CompoundTag putAll(CompoundTag other) {
        for (Map.Entry<String, Tag> entry : other) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public CompoundTag put(String key, byte[] bytes) {
        put(key, Nbt.tag(bytes));
        return this;
    }

    public CompoundTag put(String key, int[] ints) {
        put(key, Nbt.tag(ints));
        return this;
    }

    public CompoundTag put(String key, long[] longs) {
        put(key, Nbt.tag(longs));
        return this;
    }

    public CompoundTag put(String key, byte b) {
        put(key, Nbt.tag(b));
        return this;
    }

    public CompoundTag put(String key, int i) {
        put(key, Nbt.tag(i));
        return this;
    }

    public CompoundTag put(String key, double d) {
        put(key, Nbt.tag(d));
        return this;
    }

    public CompoundTag put(String key, long l) {
        put(key, Nbt.tag(l));
        return this;
    }

    public CompoundTag put(String key, short s) {
        put(key, Nbt.tag(s));
        return this;
    }

    public CompoundTag put(String key, String s) {
        put(key, Nbt.tag(s));
        return this;
    }

    public Tag get(String name) {
        return value.getOrDefault(name, NullTag.NULL);
    }

    public Tag get(String... path) {
        Tag value = this;
        for (String key : path) {
            CompoundTag parent = value.asCompound();
            if (parent.isPresent()) {
                value = parent.get(key);
            } else {
                return NullTag.NULL;
            }
        }
        return value;
    }

    public ByteArrayTag getByteArray(String name) {
        return get(name).asByteArray();
    }

    public ByteTag getByte(String name) {
        return get(name).asByte();
    }

    public CompoundTag getCompound(String name) {
        return get(name).asCompound();
    }

    public DoubleTag getDouble(String name) {
        return get(name).asDouble();
    }

    public IntArrayTag getIntArray(String name) {
        return get(name).asIntArray();
    }

    public IntTag getInt(String name) {
        return get(name).asInt();
    }

    public LongArrayTag getLongArray(String name) {
        return get(name).asLongArray();
    }

    public LongTag getLong(String name) {
        return get(name).asLong();
    }

    public ShortTag getShort(String name) {
        return get(name).asShort();
    }

    public StringTag getString(String name) {
        return get(name).asString();
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> ListTag<T> getList(String name, TagType childType) {
        Tag tag = value.get(name);
        if (tag == null) {
            return ListTag.empty();
        }
        if (tag.isAbsent()) {
            return ListTag.empty();
        }
        if (!(tag instanceof ListTag)) {
            return ListTag.empty();
        }
        ListTag list = (ListTag) tag;
        if (list.getChildType() != childType) {
            return ListTag.empty();
        }
        return (ListTag<T>) list;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CompoundTag)) return false;
        if (!super.equals(obj)) return false;
        CompoundTag compoundTag = (CompoundTag) obj;
        return Objects.equals(value, compoundTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return getValueString();
    }

    @Override
    public Iterator<Map.Entry<String, Tag>> iterator() {
        return value.entrySet().iterator();
    }
}
