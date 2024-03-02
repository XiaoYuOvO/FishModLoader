package net.xiaoyu233.fml.util;

import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import net.fabricmc.tinyremapper.extension.mixin.MixinExtension;
import net.xiaoyu233.fml.FishModLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

public class ModRemapper {
    public static void main(String[] args) throws IOException {
       if (args.length != 3){
           System.out.println("ModRemapper Usage: ModRemapper <InputModJar> <OutputModJar> <ReferenceClassesPath>");
           System.out.println("For <ReferenceClassesPath>, you should input the path to directory that contains the classes dumped from FishModLoader v1.5.0 without any mods");
       }else {
           File input = new File(args[0]);
           if (!input.exists()){
               System.err.println("Invalid input jar: " + input);
               System.exit(-1);
           }
           File refClassPath = new File(args[2]);
            if (!refClassPath.exists()){
                System.err.println("Invalid reference class path: " + refClassPath);
                System.exit(-1);
            }

           TinyRemapper remapper = TinyRemapper.newRemapper()
                   .checkPackageAccess(true)
                   .ignoreConflicts(true)
                   .extension(new MixinExtension())
                   .fixPackageAccess(true)
                   .withMappings(TinyUtils.createTinyMappingProvider(new BufferedReader(new InputStreamReader(Objects.requireNonNull(ModRemapper.class.getResourceAsStream("/migrate.tiny")))), "left", "right"))
                   .build();

           FishModLoader.LOGGER.info("Remapping mod jar with TinyRemapper on FML version " + FishModLoader.VERSION);
           Path outPath = new File(args[1]).toPath();
           try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(outPath).build()) {
               Path inputPath = input.toPath();
               outputConsumer.addNonClassFiles(inputPath, NonClassCopyMode.UNCHANGED, remapper);
               remapper.readClassPath(refClassPath.toPath());
               remapper.readInputs(inputPath);
               remapper.apply(outputConsumer);
               FishModLoader.LOGGER.info("Mod jar has successfully remapped: " + outPath);
           } catch (IOException e) {
               throw new RuntimeException(e);
           } finally {
               remapper.finish();
           }
       }
    }

}
