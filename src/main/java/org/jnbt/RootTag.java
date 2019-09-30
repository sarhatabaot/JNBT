package org.jnbt;

public class RootTag {

    private final String name;
    private final Tag tag;

    RootTag(String name, Tag tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public Tag getTag() {
        return tag;
    }
}
