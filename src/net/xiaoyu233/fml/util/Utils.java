package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.relaunch.Launch;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Utils {
   private static final int BUFFER_SIZE = 8192;
   private static final int MAX_BUFFER_SIZE = 2147483639;
   private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
   private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public static byte[] readAllBytes(InputStream stream) throws IOException {
      int capacity = 16;
      byte[] buf = new byte[capacity];
      int nread = 0;

      while(true) {
         int n;
         while((n = stream.read(buf, nread, capacity - nread)) > 0) {
            nread += n;
         }

         if (n < 0 || (n = stream.read()) < 0) {
            return capacity == nread ? buf : Arrays.copyOf(buf, nread);
         }

         if (capacity <= 2147483639 - capacity) {
            capacity = Math.max(capacity << 1, 8192);
         } else {
            if (capacity == 2147483639) {
               throw new OutOfMemoryError("Required array size too large");
            }

            capacity = 2147483639;
         }

         buf = Arrays.copyOf(buf, capacity);
         buf[nread++] = (byte)n;
      }
   }

   public static void copy(InputStream source, OutputStream target) throws IOException {
      byte[] buf = new byte[8192];

      int n;
      while((n = source.read(buf)) > 0) {
         target.write(buf, 0, n);
      }

   }

   public static String getLibFileLocation() {
      return URLDecoder.decode(Launch.class.getProtectionDomain().getCodeSource().getLocation().getFile());
   }

   public static String getLibFileLocationRawURL() {
      return Launch.class.getProtectionDomain().getCodeSource().getLocation().getFile();
   }

   public static Map<String, InputStream> getInternalClassesFromJar(String packageName) throws IOException {
      String libFile = getLibFileLocation();
      Map<String, InputStream> classNames = new HashMap();
      if (libFile.endsWith(".jar")) {
         JarFile jarFile = new JarFile(libFile);
         Enumeration entries = jarFile.entries();

         while(entries.hasMoreElements()) {
            JarEntry entry = (JarEntry)entries.nextElement();
            String name = entry.getName();
            if (name.contains(packageName) && name.endsWith(".class")) {
               classNames.put(name, jarFile.getInputStream(entry));
            }
         }

         return classNames;
      } else {
         return getAllFilesBelow(new File(libFile + packageName), ".class");
      }
   }

   public static String calcMD5(File file) {
      try {
         InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
         Throwable var2 = null;

         try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];

            int len;
            while((len = stream.read(buf)) > 0) {
               digest.update(buf, 0, len);
            }

            String var6 = toHexString(digest.digest());
            return var6;
         } catch (Throwable var16) {
            var2 = var16;
            throw var16;
         } finally {
            if (stream != null) {
               if (var2 != null) {
                  try {
                     stream.close();
                  } catch (Throwable var15) {
                     var2.addSuppressed(var15);
                  }
               } else {
                  stream.close();
               }
            }

         }
      } catch (NoSuchAlgorithmException | IOException var18) {
         var18.printStackTrace();
         return "";
      }
   }

   public static String toHexString(byte[] data) {
      StringBuilder r = new StringBuilder(data.length * 2);
      byte[] var2 = data;
      int var3 = data.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         r.append(hexCode[b >> 4 & 15]);
         r.append(hexCode[b & 15]);
      }

      return r.toString();
   }

   public static Map<String, InputStream> getInternalClassesFromJar(Predicate<String> nameChecker) throws IOException {
      String libFile = getLibFileLocation();
      Map<String, InputStream> classNames = new HashMap();
      if (libFile.endsWith(".jar")) {
         JarFile jarFile = new JarFile(libFile);
         Enumeration entries = jarFile.entries();

         while(entries.hasMoreElements()) {
            JarEntry entry = (JarEntry)entries.nextElement();
            String name = entry.getName();
            if (nameChecker.test(name)) {
               classNames.put(name, jarFile.getInputStream(entry));
            }
         }

         return classNames;
      } else {
         return new HashMap();
      }
   }

   public static File createJar(String name) throws IOException {
      File jarFile = new File((new File(getLibFileLocation())).getParentFile(), name + ".jar");
      Manifest manifest = new Manifest();
      manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
      JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile), manifest);
      out.flush();
      out.close();
      return jarFile;
   }

   public static Map<String, InputStream> getAllFilesBelow(File dir, String ext) throws FileNotFoundException {
      Map<String, InputStream> files = new HashMap();
      if (dir.isDirectory()) {
         File[] files1 = dir.listFiles();
         if (files1 != null) {
            File[] var4 = files1;
            int var5 = files1.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File listFile = var4[var6];
               if (listFile.isFile() && listFile.getName().endsWith(ext)) {
                  files.put(listFile.getAbsolutePath().replace(dir.getAbsolutePath(), ""), new FileInputStream(listFile));
               } else {
                  files.putAll(getAllFilesBelow(listFile, ext));
               }
            }
         }
      }

      return files;
   }

   public static String getInternalNameFromDesc(String desc) {
      int startIndex = 1;

      while(true) {
         char c = desc.charAt(startIndex);
         if (c != '[' && c != 'L') {
            return desc.substring(startIndex, desc.length() - 1);
         }

         ++startIndex;
      }
   }

   public static <R> R safeMake(Utils.DangerConsumer<R> maker, R defaultResult) {
      try {
         return maker.dangerGet();
      } catch (Throwable var3) {
         var3.printStackTrace();
         return defaultResult;
      }
   }

   public static void logInfoConsole(String msg) {
      System.out.println(format.format(new Date()) + " [INFO] " + msg);
   }

   public static boolean isJavaType(String name) {
      return !name.endsWith(";");
   }

   public interface DangerConsumer<R> {
      R dangerGet() throws Throwable;
   }
}
