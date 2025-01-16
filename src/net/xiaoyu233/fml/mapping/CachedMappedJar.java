package net.xiaoyu233.fml.mapping;

import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.xiaoyu233.fml.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CachedMappedJar {
    private static final Logger LOGGER = LogManager.getLogger("GameRemapper");
    private final Path jarSource;
    private final Path cacheDir;
    private final TinyRemapper remapper;

    public CachedMappedJar(Path jarSource, IMappingProvider provider, File minecraftDir) throws IOException {
        this.jarSource = jarSource;
        TinyRemapper.Builder builder = TinyRemapper.newRemapper()
                .withMappings(provider)
                .ignoreConflicts(true)
                .checkPackageAccess(true);
        this.remapper = builder.build();
        this.cacheDir = minecraftDir.toPath().resolve(".fml").resolve("remappedJars");
        Files.createDirectories(cacheDir);
    }

    public Path ensureJarMapped() {
        Path mappedJar = this.cacheDir.resolve(jarSource.getFileName() + "-" + Constants.VERSION + ".jar");
        boolean injectionsInvalid = false;

        if (Files.exists(mappedJar) && !injectionsInvalid){
            LOGGER.info("Found mapped jar cache");
            return mappedJar;
        }else {
                LOGGER.info("Mapped jar cache not found, remapping with TinyRemapper on FML version " + Constants.VERSION);
            try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(mappedJar).build()) {
                outputConsumer.addNonClassFiles(this.jarSource, NonClassCopyMode.UNCHANGED, remapper);
                this.remapper.readInputs(this.jarSource);
                remapper.apply(outputConsumer);
                LOGGER.info("Minecraft jar has successfully remapped");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                remapper.finish();
            }
        }
        return mappedJar;
    }
}
