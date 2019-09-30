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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class StringTag extends Tag<String> {

    static final Charset CHARSET = StandardCharsets.UTF_8;
    static final StringTag EMPTY = new StringTag("null");

    private final String value;

    StringTag(String value) {
        this.value = value;
    }

    StringTag(String value, TagType type) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return this != EMPTY;
    }

    @Override
    public StringTag asString() {
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    TagType<String, StringTag> getType() {
        return TagType.STRING;
    }

    @Override
    void writeValue(DataOutput out) throws IOException {
        byte[] bytes = value.getBytes(StringTag.CHARSET);
        out.writeShort(bytes.length);
        out.write(bytes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof StringTag)) return false;
        if (!super.equals(obj)) return false;
        StringTag stringTag = (StringTag) obj;
        return Objects.equals(value, stringTag.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    static String readString(DataInput in) throws IOException {
        short length = in.readShort();
        byte[] bytes = new byte[length];
        in.readFully(bytes);
        return new String(bytes, StringTag.CHARSET);
    }
}
