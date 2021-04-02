package net.xiaoyu233.fml.reload.event;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class FieldManager {
    private final Map<FieldHandler<?>, Object> fieldMap = new HashMap<>();

    public <T> void register(FieldHandler<T> handler,T initValue){
        this.fieldMap.put(handler,initValue);
    }

    public <T> void set(FieldHandler<T> handler,T value){
        this.fieldMap.replace(handler,value);
    }

    @Nonnull
    public <T> T getField(FieldHandler<T> handler){
        Object o = this.fieldMap.get(handler);
        if (o != null){
            return (T) o;
        }else {
            throw new IllegalArgumentException("Target field: " + handler.toString() + "haven't been registered");
        }
    }
}
