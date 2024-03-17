package net.xiaoyu233.fml.relaunch;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.xiaoyu233.fml.classloading.KnotClassLoaderInterface;
import net.xiaoyu233.fml.classloading.LibClassifier;
import net.xiaoyu233.fml.classloading.McLibrary;
import net.xiaoyu233.fml.util.LoaderUtil;
import net.xiaoyu233.fml.util.UrlUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LaunchDelegate {
    //Here we transpose from the AppClassLoader into our own modifiable KnotClassLoader, so here is a barrier
    public static void delegateLaunch(String mainClass, String[] args, boolean server) throws ClassNotFoundException, NoSuchMethodException, IOException, ModResolutionException {
        List<Path> classPath = new ArrayList<>();
        List<String> missing = null;
        List<String> unsupported = null;
        Path gameJarPath = UrlUtil.asPath(LaunchDelegate.class.getClassLoader().loadClass(mainClass).getProtectionDomain().getCodeSource().getLocation());
        for (String cpEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (cpEntry.equals("*") || cpEntry.endsWith(File.separator + "*")) {
                if (unsupported == null) unsupported = new ArrayList<>();
                unsupported.add(cpEntry);
                continue;
            }

            Path path = Paths.get(cpEntry);

            if (!Files.exists(path)) {
                if (missing == null) missing = new ArrayList<>();
                missing.add(cpEntry);
                continue;
            }

            if (!gameJarPath.equals(path)) {
                classPath.add(LoaderUtil.normalizeExistingPath(path));
            }
        }
        KnotClassLoaderInterface modClassLoader = KnotClassLoaderInterface.create();
        //NO!!! You shouldn't do like this, should lock classpath for preinit, and unlock it later
        classPath.forEach(modClassLoader::addCodeSource);
        LaunchClassBlocker.blockClass(modClassLoader);
        modClassLoader.setValidParentClassPath(new LibClassifier<>(McLibrary.class, server ? EnvType.SERVER : EnvType.CLIENT).getSystemLibraries());
        Launch.launch(modClassLoader, mainClass, args, server, gameJarPath);
    }
}
