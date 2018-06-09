package org.jnbt;

import java.io.DataInput;
import java.io.IOException;
import java.util.*;

interface TagReader {

    Tag read(DataInput in) throws IOException;

    TagReader NULL = in -> NullTag.NULL;
    TagReader END = in -> EndTag.END;
    TagReader BYTE = in -> new ByteTag(in.readByte());
    TagReader DOUBLE = in -> new DoubleTag(in.readDouble());
    TagReader FLOAT = in -> new FloatTag(in.readFloat());
    TagReader INT = in -> new IntTag(in.readInt());
    TagReader LONG = in -> new LongTag(in.readLong());
    TagReader SHORT = in -> new ShortTag(in.readShort());
    TagReader STRING = in -> new StringTag(StringTag.readString(in));

    TagReader BYTE_ARRAY = in -> {
        int length = in.readInt();
        byte[] array = new byte[length];
        in.readFully(array);
        return new ByteArrayTag(array);
    };

    TagReader INT_ARRAY = in -> {
        int length = in.readInt();
        int[] array = new int[length];
        int pos = 0;
        while (pos < length) {
            array[pos++] = in.readInt();
        }
        return new IntArrayTag(array);
    };

    TagReader LONG_ARRAY = in -> {
        int length = in.readInt();
        long[] array = new long[length];
        int pos = 0;
        while (pos < length) {
            array[pos++] = in.readLong();
        }
        return new LongArrayTag(array);
    };

    TagReader COMPOUND = in -> {
        Map<String, Tag> map = Collections.emptyMap();
        while (true) {
            int typeId = in.readByte();
            if (typeId == TagType.END.getId()) {
                break;
            }
            TagType type = TagType.forId(typeId);
            String key = StringTag.readString(in);
            Tag child = type.getReader().read(in);
            if (map.isEmpty()) {
                map = new LinkedHashMap<>(16);
            }
            map.put(key, child);
        }
        return new CompoundTag(map).immutable();
    };

    @SuppressWarnings("unchecked")
    TagReader LIST = in -> {
        int childTypeId = in.readByte();
        TagType<?, ?> childType = TagType.forId(childTypeId);

        int length = in.readInt();
        if (length == 0) {
            return new ListTag(Collections.emptyList(), childType);
        }

        List<Tag<?>> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Tag tag = childType.getReader().read(in);
            list.add(tag);
        }

        return new ListTag(list, childType).immutable();
    };

    static RootTag readRootTag(DataInput in) throws IOException {
        int typeId = in.readByte();
        TagType type = TagType.forId(typeId);
        String name = StringTag.readString(in);
        Tag tag = type.getReader().read(in);
        return new RootTag(name, tag);
    }
}
