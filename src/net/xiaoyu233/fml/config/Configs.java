package net.xiaoyu233.fml.config;

import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.Constants;
import net.xiaoyu233.fml.util.FieldReference;

import java.io.File;

import static net.xiaoyu233.fml.FishModLoader.CONFIG_REGISTRY;

public class Configs {

    public static final ConfigRoot CONFIG = new ConfigRoot(Constants.FML_CONFIG_VERSION).withComment("FishModLoader配置文件").
            addEntry(new ConfigCategory("Server").withComment("服务端")
                    .addEntry(new ConfigEntry<>("allowClientMods", Codec.BOOLEAN, true, Server.ALLOW_CLIENT_MODS).withComment("允许客户端使用客户端模组(如坐标器)"))).
            addEntry(new ConfigCategory("Client").withComment("客户端")
                    .addEntry(new ConfigEntry<>("fpsLimit", Codec.INTEGER, 120, Client.FPS_LIMIT).withComment("FPS最大值"))).
            addEntry(new ConfigCategory("Debug").withComment("调试").
                    addEntry(new ConfigEntry<>("debug", Debug.DEBUG).withComment("开启调试模式")).
                    addEntry(new ConfigEntry<>("print_entity_damage_info", Debug.PRINT_ENTITY_DAMAGE_INFO).withComment("输出实体受到伤害信息")).
                    addEntry(new ConfigEntry<>("printClassloadInfo", Codec.BOOLEAN, false, Debug.PRINT_CLASSLOAD_INFO).withComment("输出类加载信息")).
                    addEntry(new ConfigCategory("DumpClass").withComment("类导出(注意! 类导出将在未来逐步禁用!并尝试通过接口注入的方式替换类导出实现的功能!请尽量不要依赖类导出构建您的模组,这会使得迁移困难!)").
                            addEntry(new ConfigEntry<>("dumpPath", Codec.FILE, new File("./.mixin.out"), Debug.DumpClass.DUMP_PATH).withComment("类文件输出目录(现在可以通过导入.fml/remappedJars/下的jar文件作为编译依赖而不需类导出)")).
                            addEntry(new ConfigEntry<>("dumpClass", Codec.BOOLEAN, false, Debug.DumpClass.DUMP_CLASS).withComment("输出Mixin处理后的类文件"))));
    public static final File CONFIG_FILE = new File("fishmodloader.json");

    public static void loadConfig(){
        FishModLoader.addConfigRegistry(CONFIG_REGISTRY);
        CONFIG_REGISTRY.reloadConfig();
    }

    public static class Client{
        public static final FieldReference<Integer> FPS_LIMIT = new FieldReference<>(120);
    }

    public static class Debug{
        public static final FieldReference<Boolean> DEBUG = new FieldReference<>(false);
        public static final FieldReference<Boolean> PRINT_CLASSLOAD_INFO = new FieldReference<>(false);
        public static final FieldReference<Boolean> PRINT_ENTITY_DAMAGE_INFO = new FieldReference<>(true);

        @Deprecated
        public static class DumpClass{
            @Deprecated
            public static final FieldReference<Boolean> DUMP_CLASS = new FieldReference<>(false);
            @Deprecated
            public static final FieldReference<File> DUMP_PATH = new FieldReference<>(new File(""));
        }
    }

    public static class Server{
        public static final FieldReference<Boolean> ALLOW_CLIENT_MODS = new FieldReference<>(true);
    }
}
