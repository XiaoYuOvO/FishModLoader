package net.xiaoyu233.fml.util;

public class WriteLockField<T> {
    private boolean locked = false;
    private T value;

    private WriteLockField(T initialValue){
        value = initialValue;
    }

    public static <T> WriteLockField<T> create(T initialValue){
        return new WriteLockField<>(initialValue);
    }

    public void set(T t){
        if (locked){
            throw new IllegalStateException("Tried to set a locked field");
        }
        value = t;
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }

    public T get() {
        return value;
    }
}
