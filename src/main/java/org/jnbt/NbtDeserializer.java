package org.jnbt;

/**
 * @author dags <dags@dags.me>
 */
public interface NbtDeserializer<T> {

    T apply(CompoundTag tag);

    default T apply(Tag tag) {
        CompoundTag compound = tag.asCompound();
        if (compound.isPresent()) {
            return apply(compound);
        }
        return null;
    }

    default T apply(Tag tag, T defaultValue) {
        if (tag.isAbsent()) {
            return defaultValue;
        }

        T value = apply(tag);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }
}
