package net.xiaoyu233.fml.classloading.dump;

import net.xiaoyu233.fml.config.ConfigCategory;
import net.xiaoyu233.fml.config.ConfigEntry;
import net.xiaoyu233.fml.util.FieldReference;

public enum DumpStage {
    ACCESS_WIDENER(false),
    CLASS_TINKER(false),
    MIXIN(true);
    private final FieldReference<Boolean> shouldDump;

    DumpStage(boolean shouldDumpByDefault){
        shouldDump = new FieldReference<>(shouldDumpByDefault);
    }

    public static ConfigCategory makeExportCategory(){
        ConfigCategory dumpStages = new ConfigCategory("dumpStages");
        for (DumpStage value : DumpStage.values()) {
            dumpStages = dumpStages.addEntry(new ConfigEntry<>(value.name(), value.shouldDump).withComment("是否输出" + value.name().toLowerCase() + "阶段处理后的类文件"));
        }
        return dumpStages;
    }

    public boolean shouldDump(){
        return shouldDump.get();
    }
}
