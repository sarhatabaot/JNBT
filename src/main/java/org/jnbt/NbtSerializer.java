package org.jnbt;

/**
 * @author dags <a href="dags@dags.me"></a>
 */
public interface NbtSerializer<T> {

    void apply(T value, CompoundTag tag);

    default Tag apply(T value) {
        CompoundTag root = Nbt.compound();
        apply(value, root);
        return root;
    }
}
