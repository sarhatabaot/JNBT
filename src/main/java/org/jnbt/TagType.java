package org.jnbt;

import java.util.function.BiFunction;

/**
 * @author dags <dags@dags.me>
 */
public final class TagType<V, T extends Tag> {

    private static final TagType[] TYPES = new TagType[12];

    public static final TagType<Object, NullTag> NULL = new TagType<>(-1, "null", Object.class, TagReader.NULL);
    public static final TagType<Object, EndTag> END = new TagType<>(0, "end", Object.class, TagReader.END);
    public static final TagType<Byte, ByteTag> BYTE = new TagType<>(1, byte.class, TagReader.BYTE, ByteTag::new);
    public static final TagType<Short, ShortTag> SHORT = new TagType<>(2, short.class, TagReader.SHORT, ShortTag::new);
    public static final TagType<Integer, IntTag> INT = new TagType<>(3, int.class, TagReader.INT, IntTag::new);
    public static final TagType<Long, LongTag> LONG = new TagType<>(4, long.class, TagReader.LONG, LongTag::new);
    public static final TagType<Float, FloatTag> FLOAT = new TagType<>(5, float.class, TagReader.FLOAT, FloatTag::new);
    public static final TagType<Double, DoubleTag> DOUBLE = new TagType<>(6, double.class, TagReader.DOUBLE, DoubleTag::new);
    public static final TagType<byte[], ByteArrayTag> BYTE_ARRAY = new TagType<>(7, byte[].class, TagReader.BYTE_ARRAY, ByteArrayTag::new);
    public static final TagType<String, StringTag> STRING = new TagType<>(8, String.class, TagReader.STRING, StringTag::new);
    public static final TagType<ListTag, ListTag> LIST = new TagType<>(9, "list", ListTag.class, TagReader.LIST);
    public static final TagType<CompoundTag, CompoundTag> COMPOUND = new TagType<>(10, "compound", CompoundTag.class, TagReader.COMPOUND);
    public static final TagType<int[], IntArrayTag> INT_ARRAY = new TagType<>(11, int[].class, TagReader.INT_ARRAY, IntArrayTag::new);
    public static final TagType<long[], LongArrayTag> LONG_ARRAY = new TagType<>(12, long[].class, TagReader.LONG_ARRAY, LongArrayTag::new);

    private final byte id;
    private final String name;
    private final Class<V> type;
    private final TagReader reader;
    private final BiFunction<V, TagType<V, T>, Tag<V>> constructor;

    private TagType(int id, String name, Class<V> type, TagReader reader) {
        this(id, name, type, reader, nullConstructor());
    }

    private TagType(int id, Class<V> type, TagReader reader, BiFunction<V, TagType<V, T>, Tag<V>> constructor) {
        this(id, getName(type), type, reader, constructor);
    }

    private TagType(int id, String name, Class<V> type, TagReader reader, BiFunction<V, TagType<V, T>, Tag<V>> constructor) {
        this.id = (byte) id;
        this.name = name;
        this.type = type;
        this.reader = reader;
        this.constructor = constructor;
        if (id >= 0 && id < TYPES.length) {
            TYPES[id] = this;
        }
    }

    byte getId() {
        return id;
    }

    String getName() {
        return name;
    }

    Class<V> getType() {
        return type;
    }

    TagReader getReader() {
        return reader;
    }

    @SuppressWarnings("unchecked")
    Tag<V> write(V val) {
        if (val instanceof Tag) {
            Tag tag = (Tag) val;
            if (tag.getType() == this) {
                return (Tag<V>) val;
            }
        }
        return constructor.apply(val, this);
    }

    static TagType forId(int id) {
        return TYPES[id];
    }

    private static <V, T extends Tag> BiFunction<V, TagType<V, T>, Tag<V>> nullConstructor() {
        return (v, t)  -> NullTag.empty();
    }

    private static String getName(Class<?> c) {
        if (c.isArray()) {
            c = c.getComponentType();
        }
        return c.getSimpleName().toLowerCase();
    }
}
