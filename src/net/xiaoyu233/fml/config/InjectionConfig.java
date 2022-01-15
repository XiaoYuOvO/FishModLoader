package net.xiaoyu233.fml.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.PackageLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.transformer.MixinConfig;
import org.spongepowered.asm.service.IMixinService;

import java.util.List;

public class InjectionConfig {
    private static final Gson gson = new Gson();
    private final MixinEnvironment.CompatibilityLevel compatibleLevel;
    private final int defaultRequiredInjection;
    private final int mixinPriority;
    private final String minMixinVersion;
    private final String name;
    private final boolean required;
    private final MixinEnvironment.Phase targetPhase;
    private final Package transformPackage;

    public InjectionConfig(Package transformPackage, MixinEnvironment.CompatibilityLevel compatibleLevel, int mixinPriority, MixinEnvironment.Phase targetPhase, String minMixinVersion, int defaultRequiredInjection, boolean required, String name) {
        this.transformPackage = transformPackage;
        this.compatibleLevel = compatibleLevel;
        this.mixinPriority = mixinPriority;
        this.targetPhase = targetPhase;
        this.minMixinVersion = minMixinVersion;
        this.defaultRequiredInjection = defaultRequiredInjection;
        this.required = required;
        this.name = name;
    }

    public MixinEnvironment.CompatibilityLevel getCompatibleLevel() {
        return compatibleLevel;
    }

    public int getDefaultRequiredInjection() {
        return defaultRequiredInjection;
    }

    public String getMinMixinVersion() {
        return minMixinVersion;
    }

    public MixinEnvironment.Phase getTargetPhase() {
        return targetPhase;
    }

    public Package getTransformPackage() {
        return transformPackage;
    }

    public boolean isRequired() {
        return required;
    }

    public org.spongepowered.asm.mixin.transformer.Config toConfig(ClassLoader classLoader, IMixinService mixinService, MixinEnvironment environment){
        MixinConfig config = this.toMixinConfig(classLoader);
        config.onLoad(mixinService,this.name,environment);
        return new org.spongepowered.asm.mixin.transformer.Config(config);
    }

    private MixinConfig toMixinConfig(ClassLoader classLoader){
        JsonObject mixinObject = new JsonObject();
        mixinObject.addProperty("required",this.required);
        String pkgName = this.transformPackage.getName();
        mixinObject.addProperty("package", pkgName);
        mixinObject.addProperty("compatibilityLevel",this.compatibleLevel.toString());
        JsonArray mixins = new JsonArray();
        List<String> classes = PackageLoader.getClasses(pkgName, classLoader,Mixin.class);
        for (String aClass : classes) {
            String clName = aClass.replace(pkgName + ".", "");
            FishModLoader.LOGGER.info("Registering mixin class:" + clName + " for " + this.name);
            mixins.add(new JsonPrimitive(clName));
        }
        mixinObject.addProperty("mixinPriority",this.mixinPriority);
        mixinObject.add("mixins",mixins);
        JsonObject injectors = new JsonObject();
        injectors.addProperty("defaultRequire",this.defaultRequiredInjection);
        mixinObject.add("injectors",injectors);
        mixinObject.addProperty("minVersion",this.minMixinVersion);
        mixinObject.addProperty("target","@env(" + this.targetPhase.toString() + ")");
        return gson.fromJson(mixinObject,MixinConfig.class);
    }

    public static class Builder{
        private final String name;
        private final MixinEnvironment.Phase targetPhase;
        private final Package transformPackage;
        private  MixinEnvironment.CompatibilityLevel compatibleLevel = MixinEnvironment.CompatibilityLevel.JAVA_8;
        private  int defaultRequiredInjection = 0;
        private  String minMixinVersion = MixinEnvironment.getDefaultEnvironment().getVersion();
        private  boolean required = false;
        private int mixinPriority = IMixinConfig.DEFAULT_PRIORITY;
        private Builder(String name, Package transformPackage, MixinEnvironment.Phase targetPhase) {
            this.name = name;
            this.transformPackage = transformPackage;
            this.targetPhase = targetPhase;
        }

        public static Builder of(String name,Package transformPackage, MixinEnvironment.Phase targetPhase){
            return new Builder(name, transformPackage, targetPhase);
        }

        public InjectionConfig build(){
            return new InjectionConfig(this.transformPackage,this.compatibleLevel, this.mixinPriority, this.targetPhase,this.minMixinVersion,this.defaultRequiredInjection,this.required, name);
        }

        public Builder setCompatibleLevel(MixinEnvironment.CompatibilityLevel compatibleLevel) {
            this.compatibleLevel = compatibleLevel;
            return this;
        }

        public Builder setDefaultRequiredInjection(int defaultRequiredInjection) {
            this.defaultRequiredInjection = defaultRequiredInjection;
            return this;
        }

        public Builder setMinMixinVersion(String minMixinVersion) {
            this.minMixinVersion = minMixinVersion;
            return this;
        }

        public Builder setMixinPriority(int priority){
            this.mixinPriority = priority;
            return this;
        }

        public Builder setRequired() {
            this.required = true;
            return this;
        }
    }
}
