package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.mapping.Remapping;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MapConnector {
   public static void main(String[] args) throws IOException {
      Remapping remapping = new Remapping();
      remapping.addMappingFromFile(new File("H:\\IDEAProjects\\FishModLoader\\src\\class.mapping"), Remapping.MappingType.CLASS);
      remapping.addMappingFromFile(new File("H:\\IDEAProjects\\FishModLoader\\src\\method.mapping"), Remapping.MappingType.METHOD);
      remapping.addMappingFromFile(new File("H:\\IDEAProjects\\FishModLoader\\src\\field.mapping"), Remapping.MappingType.FIELD);
      StringBuilder out = new StringBuilder();
      String path = "F:\\MITE Plus R5\\.minecraft\\versions\\1.6.4-MITE\\1.6.4-MITE.jar";
      JarFile minecraftJar = new JarFile(path);
      Enumeration<JarEntry> entries = minecraftJar.entries();

      do {
         JarEntry entry;
         do {
            if (!entries.hasMoreElements()) {
               System.out.println(out.toString());
               return;
            }

            entry = entries.nextElement();
         } while (!entry.getName().endsWith(".class"));

         ClassReader reader = new ClassReader(minecraftJar.getInputStream(entry));
         ClassNode classNode = new ClassNode();
         reader.accept(classNode, 0);
         remapping.addSuperclassMapping(classNode.name, classNode.superName);
         remapping.addInterfaceMap(classNode.name, classNode.interfaces);

         for (FieldNode field : classNode.fields) {
            out.append(classNode.name).append(".").append(field.name).append(field.desc).append(" : ").append(remapping.mapFieldName(classNode.name, field.name, field.desc)).append("\n");
         }
      } while (true);
   }
}
