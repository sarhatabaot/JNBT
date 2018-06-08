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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class ListTag<T extends Tag> extends Tag implements Iterable<T> {

    private static final ListTag EMPTY = new ListTag<>(NullTag.NULL.getType(), Collections.emptyList());

    private final TagType childType;
    private final List<T> value;

    ListTag(TagType childType, List<T> value) {
        this.childType = childType;
        this.value = value;
    }

    public ListTag<T> copy() {
        return new ListTag<>(childType, new ArrayList<>(value));
    }

    public ListTag<T> immutable() {
        return new ListTag<>(childType, Collections.unmodifiableList(value));
    }

    public ListTag<T> immutableCopy() {
        return copy().immutable();
    }

    public ListTag<T> add(T tag) {
        if (tag.isPresent()) {
            value.add(tag);
        }
        return this;
    }

    public ListTag<T> addAll(Iterable<T> tags) {
        for (T t : tags) {
            add(t);
        }
        return this;
    }

    TagType getChildType() {
        return childType;
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
    public List<T> getValue() {
        return value;
    }

    @Override
    String getValueString() {
        return value.toString();
    }

    @Override
    public TagType getType() {
        return TagType.LIST;
    }

    @Override
    void writeValue(DataOutput out) throws IOException {
        out.writeByte(childType.getId());
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
        return Objects.equals(childType, listTag.childType) && Objects.equals(value, listTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), childType, value);
    }

    @Override
    public Iterator<T> iterator() {
        return value.iterator();
    }

    @Override
    public String toString() {
        return getValueString();
    }

    @SuppressWarnings("unchecked")
    static <T extends Tag> ListTag<T> empty() {
        return (ListTag<T>) EMPTY;
    }
}
