package net.xiaoyu233.fml.relaunch.server;

import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.fabricmc.loader.impl.util.FileSystemUtil;
import net.fabricmc.loader.impl.util.UrlUtil;
import net.xiaoyu233.fml.classloading.KnotClassLoaderInterface;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class JarMain {

    public static void main(String[] args) throws IOException, ModResolutionException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String path = "";
        Path fmlPath = UrlUtil.asPath(JarMain.class.getProtectionDomain().getCodeSource().getLocation())
                .normalize()
                .toAbsolutePath();
        path += fmlPath.toString();
        Path minecraftSearchPath = Paths.get("").toAbsolutePath();
//        Path guavaJar = findJarInPath(minecraftSearchPath, "com/google/common/graph/ValueGraph.class", "guava-28.0");
        Path minecraftJar = findJarInPath(minecraftSearchPath, "net/minecraft/server/MinecraftServer.class", "MITE-HDS");
        path += ";" + minecraftJar.normalize().toAbsolutePath();
        System.setProperty("java.class.path", path);
        KnotClassLoaderInterface modClassLoader = KnotClassLoaderInterface.create();
//        modClassLoader.addCodeSource(guavaJar);
        modClassLoader.addCodeSource(fmlPath);
        modClassLoader.addCodeSource(minecraftJar);
        modClassLoader.loadIntoTarget("net.xiaoyu233.fml.relaunch.server.Main").getMethod("main",String[].class).invoke(null, (Object) args);
    }

    @Nonnull
    private static Path findJarInPath(Path searchPath, String identityFilePath, String name) throws IOException {
        List<Path> availableJars;
        try (Stream<Path> list = Files.list(searchPath)) {
            availableJars = list.filter(path1 -> {
                if (path1.getFileName().toString().endsWith("jar")) {
                    try {
                        FileSystemUtil.FileSystemDelegate jarFileSystem = FileSystemUtil.getJarFileSystem(path1, false);
                        return Files.exists(jarFileSystem.get().getPath(identityFilePath));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return false;
            }).toList();
        }
        if (availableJars.size() > 1){
            throw new IllegalStateException("Found more than one available " + name + " jars, only one is required: " + availableJars);
        }else if (availableJars.isEmpty()){
            System.err.println("Cannot find available jar " + name + " in the working dir:" + searchPath);
            System.out.println("Trying to extract " + name + " from loader");
            try (InputStream resourceAsStream = JarMain.class.getResourceAsStream("/" + name + ".jar")){
                if (resourceAsStream == null) throw new FileNotFoundException("Cannot find " + name + " jar in the loader jar");
                Path outPath = searchPath.resolve(name + ".jar");
                Files.copy(resourceAsStream, outPath);
                return outPath;
            } catch (IOException e) {
                throw new IOException("Cannot extract " + name + " jar from loader", e);
            }
        }
        return availableJars.get(0);
    }
}
