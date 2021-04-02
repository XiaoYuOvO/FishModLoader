package net.xiaoyu233.fml.reload.event;

import java.util.Objects;

public class FieldHandler<T> {
    private final Class<T> type;
    private final String name;

    public FieldHandler(Class<T> type,String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldHandler<?> that = (FieldHandler<?>) o;
        return Objects.equals(type, that.type) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return "FieldHandler{" +
                "type=" + type.getName()+
                ", name='" + name + '\'' +
                '}';
    }
}
