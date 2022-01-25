package net.xiaoyu233.fml.config;

import net.xiaoyu233.fml.util.FieldReference;

import java.io.File;

public class Configs {

    public static final ConfigCategory CONFIG = new ConfigCategory("root").
            addEntry(new ConfigCategory("Server").
                    addEntry(new ConfigEntry<>("allowClientMods",Codec.BOOLEAN,true,Server.allowClientMods).withComment("允许客户端使用客户端模组(如坐标器)"))).
            addEntry(new ConfigCategory("Client").
                    addEntry(new ConfigEntry<>("fpsLimit",Codec.INTEGER,120,Client.fpsLimit).withComment("FPS最大值"))).
            addEntry(new ConfigCategory("Debug").
                    addEntry(new ConfigEntry<>("debug",Debug.debug).withComment("开启调试模式")).
                    addEntry(new ConfigEntry<>("printClassloadInfo",Codec.BOOLEAN,false,Debug.printClassloadInfo).withComment("输出类加载信息")).
                    addEntry(new ConfigCategory("DumpClass").
                            addEntry(new ConfigEntry<>("dumpPath",Codec.FILE,new File("./.mixin.out"),Debug.DumpClass.dumpPath).withComment("类文件输出目录")).
                            addEntry(new ConfigEntry<>("dumpClass",Codec.BOOLEAN,false,Debug.DumpClass.dumpClass).withComment("输出Mixin处理后的类文件"))));
    public static final File CONFIG_FILE = new File("fishmodloader.json");

    public static void loadConfig(){
        CONFIG.readFromFile(CONFIG_FILE);
    }

    public static class Client{
        public static final FieldReference<Integer> fpsLimit = new FieldReference<>(120);
    }

    public static class Debug{
        public static final FieldReference<Boolean> debug = new FieldReference<>(false);
        public static final FieldReference<Boolean> printClassloadInfo = new FieldReference<>(false);

        public static class DumpClass{
            public static final FieldReference<Boolean> dumpClass = new FieldReference<>(false);
            public static final FieldReference<File> dumpPath = new FieldReference<>(new File(""));
        }
    }

    public static class Server{
        public static final FieldReference<Boolean> allowClientMods = new FieldReference<>(true);
    }
}
