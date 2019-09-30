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

public final class DoubleTag extends NumberTag<Double> {

    static final DoubleTag EMPTY = new DoubleTag(0D);

    private final double value;

    DoubleTag(double value) {
        this.value = value;
    }

    DoubleTag(double value, TagType type) {
        this.value = value;
    }

    @Override
    public boolean isPresent() {
        return this != EMPTY;
    }

    @Override
    public DoubleTag asDouble() {
        return this;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    TagType<Double, DoubleTag> getType() {
        return TagType.DOUBLE;
    }

    @Override
    void writeValue(DataOutput out) throws IOException {
        out.writeDouble(value);
    }

    public double doubleValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DoubleTag)) return false;
        if (!super.equals(obj)) return false;
        DoubleTag doubleTag = (DoubleTag) obj;
        return Double.compare(doubleTag.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
