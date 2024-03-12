package net.xiaoyu233.fml.asm;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class InterfaceInjection {
    private final Multimap<String, Class<?>> injections;
    private final String modId;
    private InterfaceInjection(Multimap<String, Class<?>> injections, String modId){
        this.injections = injections;
        this.modId = modId;
    }

    public String getModId() {
        return modId;
    }

    public Multimap<String, Class<?>> getInjections() {
        return injections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InterfaceInjection that = (InterfaceInjection) o;
        return Objects.equals(injections, that.injections) && Objects.equals(modId, that.modId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(injections, modId);
    }

    public static class Builder{
        private final Multimap<String, Class<?>> injections = Multimaps.newSetMultimap(new HashMap<>(), Sets::newHashSet);
        private final String modId;
        private Builder(String modId){
            this.modId = modId;
        }

        public static Builder create(String modId){
            return new Builder(modId);
        }

        /**
         * @param minecraftClassName The name of target class of minecraft in DEOBFUSCATED form
         * @param interfaces Your interface classes to inject
         */
        public Builder inject(String minecraftClassName, Class<?>... interfaces){
            injections.putAll(minecraftClassName, Arrays.asList(interfaces));
            return this;
        }

        public InterfaceInjection build(){
            return new InterfaceInjection(injections, modId);
        }

    }
}
