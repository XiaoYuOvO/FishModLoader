package net.xiaoyu233.fml.mapping;

import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import net.xiaoyu233.fml.FishModLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CachedMappedJar {
    private final URL jarSource;
    private final File minecraftDir;
    private final Path cacheDir;
    private final TinyRemapper remapper;

    public CachedMappedJar(URL jarSource, BufferedReader mappingReader, File minecraftDir) throws IOException, NoSuchAlgorithmException {
        this.jarSource = jarSource;
        this.remapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(mappingReader, "left", "right"))
                .ignoreConflicts(true)
                .checkPackageAccess(true)
                .build();
        this.minecraftDir = minecraftDir;
        this.cacheDir = minecraftDir.toPath().resolve(".fml").resolve("remappedJars");
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

    public URL ensureJarMapped() throws MalformedURLException, UnsupportedEncodingException {
        String decode = URLDecoder.decode(jarSource.getPath(), "UTF-8");
        File sourceJarFile = new File(decode);
        Path mappedJar = this.cacheDir.resolve(sourceJarFile.getName() + "-" + FishModLoader.VERSION + ".jar");
        if (Files.exists(mappedJar)){
            FishModLoader.LOGGER.info("Found mapped jar cache");
            return mappedJar.toUri().toURL();
        }else {
            FishModLoader.LOGGER.info("Mapped jar cache not found, remapping with TinyRemapper on FML version " + FishModLoader.VERSION);
            try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(mappedJar).build()) {
                Path input = sourceJarFile.toPath();
                outputConsumer.addNonClassFiles(input, NonClassCopyMode.UNCHANGED, remapper);
                this.remapper.readInputs(input);
                remapper.apply(outputConsumer);
                FishModLoader.LOGGER.info("Minecraft jar has successfully remapped");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                remapper.finish();
            }
        }
        return mappedJar.toUri().toURL();
    }
}
