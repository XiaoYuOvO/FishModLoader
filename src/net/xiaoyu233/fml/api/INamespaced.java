package net.xiaoyu233.fml.api;

public interface INamespaced {
    default String getNamespace(){throw new AssertionError();}

    default void setNamespace(String namespace){throw new AssertionError();};

    default boolean hasNamespaceSet(){throw new AssertionError();}
}
