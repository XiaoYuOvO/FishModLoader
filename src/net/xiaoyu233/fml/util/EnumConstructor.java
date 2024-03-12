package net.xiaoyu233.fml.util;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumConstructor<E extends Enum<?>> {
//    private final ConstructorAccessor constructorAccessor;
    private final List<E> values;
    private final Map<String, E> enumMaps = new HashMap<>();
    private int nextOrdinal;
    private E[] valuesCache;

    @SuppressWarnings("unchecked")
    EnumConstructor(Class<E> targetClass, int constructorIndex) {
        try {
            Constructor<?> ctor = targetClass.getDeclaredConstructors()[constructorIndex];
            Method acquireConstructorAccessor;
            acquireConstructorAccessor = Constructor.class.getDeclaredMethod("acquireConstructorAccessor");
            acquireConstructorAccessor.setAccessible(true);
            acquireConstructorAccessor.invoke(ctor);
            Field field = Constructor.class.getDeclaredField("constructorAccessor");
            field.setAccessible(true);
//            constructorAccessor = (ConstructorAccessor) field.get(ctor);
            this.valuesCache = (E[]) targetClass.getDeclaredMethod("values").invoke(null);
            values = Lists.newArrayList(valuesCache);
            nextOrdinal = values.size();
            values.forEach(e -> enumMaps.put(e.name(), e));
        }catch (Exception e){
            throw new RuntimeException("Cannot access enum constructor: " + targetClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public E create(String name, Object... args){
        //TODO Temporary disable, replace with MM
//        try {
//            Object[] constructorArgs = new Object[2 + args.length];
//            constructorArgs[0] = name;
//            constructorArgs[1] = nextOrdinal;
//            nextOrdinal++;
//            System.arraycopy(args, 0, constructorArgs, 3, args.length);
//            E format =  (E) constructorAccessor.newInstance(constructorArgs);
//            values.add(format);
//            return format;
//        } catch (InstantiationException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Nullable
    public E valueOf(String s){
        return enumMaps.get(s);
    }

    @Nonnull
    public List<E> valueList() {
        return values;
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue" })
    @Nonnull
    public E[] values(){
        if (this.valuesCache == null) this.valuesCache = (E[])values.toArray();
        return this.valuesCache;
    }
}
