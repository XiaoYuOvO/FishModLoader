package net.xiaoyu233.fml.mapping;

import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.xiaoyu233.fml.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CachedMappedJar {
    private static final Logger LOGGER = LogManager.getLogger("GameRemapper");
    private final Path jarSource;
    private final File minecraftDir;
    private final Path cacheDir;
//    private final Set<InterfaceInjection> injections;
    private final TinyRemapper remapper;

    public CachedMappedJar(Path jarSource, IMappingProvider provider, File minecraftDir
//            , Set<InterfaceInjection> injections
    ) throws IOException, NoSuchAlgorithmException {
        this.jarSource = jarSource;
        TinyRemapper.Builder builder = TinyRemapper.newRemapper()
                .withMappings(provider)
                .ignoreConflicts(true)
                .checkPackageAccess(true);
//        if (!injections.isEmpty()){
//            builder.extension(new InterfaceInjectionExtension(injections));
//        }
        this.remapper = builder.build();
        this.minecraftDir = minecraftDir;
        this.cacheDir = minecraftDir.toPath().resolve(".fml").resolve("remappedJars");
//        this.injections = injections;
        Files.createDirectories(cacheDir);
    }

    public static String calculateBufferedReaderHash(BufferedReader reader) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String line;
        while ((line = reader.readLine()) != null) {
            digest.update(line.getBytes());
        }
        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public Path ensureJarMapped() throws IOException {
        Path mappedJar = this.cacheDir.resolve(jarSource.getFileName() + "-" + Constants.VERSION + ".jar");
        boolean injectionsInvalid = false;
        //Check injection valid
//        String injectionHash = Integer.toHexString(injections.hashCode());
//        Path interfaceInjectionHash = this.cacheDir.resolve("InterfaceInjectionHash");
//        if (!this.injections.isEmpty()) {
//            if (Files.exists(interfaceInjectionHash)) {
//                List<String> strings = Files.readAllLines(interfaceInjectionHash, Charsets.UTF_8);
//                if (strings.isEmpty()){
//                    injectionsInvalid = true;
//                }else {
//                    injectionsInvalid = !strings.get(0).trim().equals(injectionHash);
//                }
//            } else {
//                injectionsInvalid = true;
//            }
//        }

        if (Files.exists(mappedJar) && !injectionsInvalid){
            LOGGER.info("Found mapped jar cache");
            return mappedJar;
        }else {
//            if (injectionsInvalid){
//                LOGGER.info("Mapped jar cache invalid by interface injections, remapping with TinyRemapper on FML version " + Constants.VERSION + " with injection hash: " + injectionHash);
//            }else {
                LOGGER.info("Mapped jar cache not found, remapping with TinyRemapper on FML version " + Constants.VERSION);
//            }
            try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(mappedJar).build()) {
                outputConsumer.addNonClassFiles(this.jarSource, NonClassCopyMode.UNCHANGED, remapper);
                this.remapper.readInputs(this.jarSource);
                remapper.apply(outputConsumer);
//                if (!injections.isEmpty()){
//                    Files.write(interfaceInjectionHash, Lists.newArrayList(injectionHash), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//                }
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
