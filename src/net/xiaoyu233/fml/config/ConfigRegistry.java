package net.xiaoyu233.fml.config;

import com.google.common.base.Objects;
import net.xiaoyu233.fml.FishModLoader;

import java.io.File;

public class ConfigRegistry {
    private final File pathToConfigFile;
    private final Config root;

    public ConfigRegistry(Config root, File pathToConfigFile) {
        this.root = root;
        this.pathToConfigFile = new File(FishModLoader.CONFIG_DIR,pathToConfigFile.toString());
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
        this.root.readFromFile(pathToConfigFile);
    }
}
