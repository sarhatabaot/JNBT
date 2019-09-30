package org.jnbt;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dags <dags@dags.me>
 */
public final class Nbt {

    private Nbt() {

    }

    public static CompoundTag compound() {
        return new CompoundTag(new LinkedHashMap<>(16));
    }

    public static CompoundTag compound(int size) {
        return new CompoundTag(new LinkedHashMap<>(size));
    }

    public static <V> ListTag<V> list(TagType<V, ? extends Tag<V>> type) {
        return new ListTag<>(new ArrayList<>(16), type);
    }

    public static <V> ListTag<V> list(TagType<V, ? extends Tag<V>> type, Iterable<V> list) {
        if (list == null) {
            return ListTag.empty();
        }
        List<Tag<V>> tags = new ArrayList<>();
        for (V v : list) {
            Tag<V> tag = type.write(v);
            if (tag.isPresent()) {
                tags.add(tag);
            }
        }
        return new ListTag<>(tags, type);
    }

    public static <V> ListTag<V> list(TagType<V, ? extends Tag<V>> type, V value) {
        return list(type, value);
    }

    @SafeVarargs
    public static <V> ListTag<V> list(TagType<V, ? extends Tag<V>> type, V... value) {
        List<Tag<V>> tags = new ArrayList<>(value.length);
        for (V v : value) {
            Tag<V> tag = type.write(v);
            if (tag.isPresent()) {
                tags.add(tag);
            }
        }
        return new ListTag<>(tags, type);
    }

    public static CompoundTag tag(Map<String, Tag> backing) {
        if (backing == null) {
            return CompoundTag.EMPTY;
        }
        return new CompoundTag(backing);
    }

    public static ByteArrayTag tag(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return ByteArrayTag.EMPTY;
        }
        return new ByteArrayTag(bytes);
    }

    public static IntArrayTag tag(int[] ints) {
        if (ints == null) {
            return IntArrayTag.EMPTY;
        }
        return new IntArrayTag(ints);
    }

    public static LongArrayTag tag(long[] longs) {
        if (longs == null) {
            return LongArrayTag.EMPTY;
        }
        return new LongArrayTag(longs);
    }

    public static ByteTag tag(boolean b) {
        return tag(b ? (byte) 1 : 0);
    }

    public static ByteTag tag(byte b) {
        return new ByteTag(b);
    }

    public static DoubleTag tag(double d) {
        return new DoubleTag(d);
    }

    public static FloatTag tag(float f) {
        return new FloatTag(f);
    }

    public static IntTag tag(int i) {
        return new IntTag(i);
    }

    public static LongTag tag(long l) {
        return new LongTag(l);
    }

    public static ShortTag tag(short s) {
        return new ShortTag(s);
    }

    public static StringTag tag(String s) {
        if (s == null) {
            return StringTag.EMPTY;
        }
        return new StringTag(s);
    }

    public static ByteTag byteTag(byte b) {
        return tag(b);
    }

    public static ByteTag byteTag(int i) {
        return tag((byte) i);
    }

    public static ByteTag byteTag(boolean b) {
        return byteTag(b ? 1 : 0);
    }

    public static DoubleTag doubleTag(double d) {
        return tag(d);
    }

    public static FloatTag floatTag(float f) {
        return tag(f);
    }

    public static FloatTag floatTag(double d) {
        return tag((float) d);
    }

    public static IntTag intTag(int i) {
        return tag(i);
    }

    public static IntTag intTag(long l) {
        return tag((int) l);
    }

    public static LongTag longTag(long l) {
        return tag(l);
    }

    public static ShortTag shortTag(short s) {
        return tag(s);
    }

    public static ShortTag shortTag(int i) {
        return tag((short) i);
    }

    public static StringTag stringTag(String s) {
        return tag(s);
    }

    public static RootTag read(InputStream in) throws IOException {
        if (in instanceof DataInput) {
            return read((DataInput) in);
        } else {
            return read((DataInput) new DataInputStream(in));
        }
    }

    public static RootTag read(DataInputStream in) throws IOException {
        return read((DataInput) in);
    }

    public static RootTag read(DataInput in) throws IOException {
        return TagReader.readRootTag(in);
    }

    public static void write(Tag tag, OutputStream out) throws IOException {
        write("", tag, out);
    }

    public static void write(Tag tag, DataOutputStream out) throws IOException {
        write("", tag, (DataOutput) out);
    }

    public static void write(Tag tag, DataOutput out) throws IOException {
        write("", tag, out);
    }

    public static void write(String name, Tag tag, OutputStream out) throws IOException {
        if (out instanceof DataOutput) {
            write(name, tag, (DataOutput) out);
        } else {
            DataOutput data = new DataOutputStream(out);
            write(name, tag, data);
        }
    }

    public static void write(String name, Tag tag, DataOutput out) throws IOException {
        if (tag.isAbsent()) {
            throw new NullPointerException("tag not present");
        }
        tag.writeTo(name, out);
    }
}
