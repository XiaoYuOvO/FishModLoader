package net.xiaoyu233.fml.config;

import com.google.common.base.Objects;
import net.xiaoyu233.fml.FishModLoader;

import java.io.File;

public class ConfigRegistry {
    private final File pathToConfigFile;
    private final File configFile;
    private final Config root;
    private Runnable reloadRun = () -> {};

    public ConfigRegistry(Config root, File pathToConfigFile) {
        this.root = root;
        this.configFile = pathToConfigFile;
        this.pathToConfigFile = new File(FishModLoader.CONFIG_DIR,pathToConfigFile.toString());
    }

    public ConfigRegistry setReloadRun(Runnable reloadRun) {
        this.reloadRun = reloadRun;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigRegistry that = (ConfigRegistry) o;
        return Objects.equal(root, that.root);
    }

    public File getPathToConfigFile() {
        return pathToConfigFile;
    }

    public Config getRoot() {
        return root;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(root);
    }

    public void reloadConfig(){
        this.root.readFromFile(configFile);
        this.reloadRun.run();
    }
}
