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
import java.util.*;

public final class ListTag<V> extends Tag<ListTag> implements Iterable<V> {

    private static final ListTag EMPTY = new ListTag<>(Collections.emptyList(), TagType.NULL);

    private final TagType<V, ? extends Tag<V>> child;
    private final List<Tag<V>> value;

    ListTag(List<Tag<V>> value, TagType<V, ? extends Tag<V>> childType) {
        this.child = childType;
        this.value = value;
    }

    public ListTag<V> copy() {
        return new ListTag<>(new ArrayList<>(value), child);
    }

    public ListTag<V> immutable() {
        return new ListTag<>(Collections.unmodifiableList(value), child);
    }

    public ListTag<V> immutableCopy() {
        return new ListTag<>(Collections.unmodifiableList(new ArrayList<>(value)), child);
    }

    public List<Tag<V>> getBacking() {
        return value;
    }

    public <T> List<T> getList(NbtDeserializer<T> deserializer) {
        List<T> list = new ArrayList<>(value.size());
        for (Tag tag : value) {
            CompoundTag compound = tag.asCompound();
            if (compound.isPresent()) {
                T t = deserializer.apply(compound);
                list.add(t);
            }
        }
        return list;
    }

    public ListTag<V> add(Tag<V> tag) {
        if (tag.isPresent()) {
            value.add(tag);
        }
        return this;
    }

    public ListTag<V> add(V value) {
        Tag<V> tag = child.create(value);
        add(tag);
        return this;
    }

    public ListTag<V> add(Iterable<V> values) {
        for (V v : values) {
            Tag<V> tag = child.create(v);
            add(tag);
        }
        return this;
    }

    public ListTag<V> addAll(ListTag<V> list) {
        addAll(list.value);
        return this;
    }

    public ListTag<V> addAll(Iterable<Tag<V>> tags) {
        for (Tag<V> t : tags) {
            add(t);
        }
        return this;
    }

    @Override
    public boolean isPresent() {
        return this != EMPTY;
    }

    @Override
    public ListTag<?> asList() {
        return this;
    }

    @Override
    public ListTag getValue() {
        return this;
    }

    @Override
    String getValueString() {
        return value.toString();
    }

    @Override
    TagType<ListTag, ListTag> getType() {
        return TagType.LIST;
    }

    TagType<V, ?> getChildType() {
        return child;
    }

    @Override
    void writeValue(DataOutput out) throws IOException {
        out.writeByte(child.getId());
        out.writeInt(value.size());
        for (Tag tag : value) {
            tag.writeValue(out);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ListTag)) return false;
        if (!super.equals(obj)) return false;
        ListTag listTag = (ListTag) obj;
        return Objects.equals(child, listTag.child) && Objects.equals(value, listTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), child, value);
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {

            private final Iterator<Tag<V>> iterator = value.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public V next() {
                return iterator.next().getValue();
            }
        };
    }

    @Override
    public String toString() {
        return getValueString();
    }

    @SuppressWarnings("unchecked")
    static <T> ListTag<T> empty() {
        return (ListTag<T>) EMPTY;
    }
}
