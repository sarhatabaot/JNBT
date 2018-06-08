package org.jnbt;

/**
 * @author dags <dags@dags.me>
 */
public enum TagType {
    NULL(-1, TagReader.NULL),
    END(0, TagReader.END),
    BYTE(1, TagReader.BYTE),
    SHORT(2, TagReader.SHORT),
    INT(3, TagReader.INTEGER),
    LONG(4, TagReader.LONG),
    FLOAT(5, TagReader.FLOAT),
    DOUBLE(6, TagReader.DOUBLE),
    BYTE_ARRAY(7, TagReader.BYTE_ARRAY),
    STRING(8, TagReader.STRING),
    LIST(9, TagReader.LIST),
    COMPOUND(10, TagReader.COMPOUND),
    INT_ARRAY(11, TagReader.INT_ARRAY),
    LONG_ARRAY(12, TagReader.LONG_ARRAY),
    ;

    private final byte id;
    private final String name;
    private final TagReader reader;

    TagType(int id, TagReader reader) {
        this.id = (byte) id;
        this.reader = reader;
        this.name = toString().toLowerCase();
    }

    TagReader getReader() {
        return reader;
    }

    String getName() {
        return name;
    }

    byte getId() {
        return id;
    }

    static TagType forId(int id) {
        return TYPES[id];
    }

    private static final TagType[] TYPES = new TagType[LONG_ARRAY.getId() + 1];

    static {
        TagType[] values = values();
        for (TagType type : values) {
            if (type.getId() >= 0 && type.getId() < TYPES.length) {
                TYPES[type.getId()] = type;
            }
        }
    }
}
