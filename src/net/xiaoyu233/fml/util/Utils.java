package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.asm.Transformer;

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

            if (capacity <= MAX_BUFFER_SIZE - capacity) {
                capacity = Math.max(capacity << 1, BUFFER_SIZE);
            } else {
                if (capacity == MAX_BUFFER_SIZE) {
                    throw new OutOfMemoryError("Required array size too large");
                }

                capacity = MAX_BUFFER_SIZE;
            }

            buf = Arrays.copyOf(buf, capacity);
            buf[nread++] = (byte)n;
        }
    }

    public static void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];

        int n;
        while((n = source.read(buf)) >0) {
            target.write(buf, 0, n);
        }

    }

    public static void extractFileFromJar(String path,File outFile,boolean override) throws IOException {
        if (outFile.exists() && !override){
            return;
        }
        InputStream resourceAsStream = Utils.class.getResourceAsStream(path);
        outFile.mkdirs();
        FileOutputStream fos = new FileOutputStream(outFile);
        copy(resourceAsStream, fos);
        resourceAsStream.close();
        fos.close();
    }

    public static String getLibFileLocation(){
        return URLDecoder.decode(Transformer.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    public static String getLibFileLocationRawURL(){
        return Transformer.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    }

    public static Map<String,InputStream> getInternalClassesFromJar(String packageName) throws IOException {

        String libFile = getLibFileLocation();
        Map<String,InputStream> classNames = new HashMap<>();
        if (libFile.endsWith(".jar")) {
            JarFile jarFile;
            jarFile = new JarFile(libFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.contains(packageName) && name.endsWith(".class")){
                    classNames.put(name,jarFile.getInputStream(entry));
                }
            }
            return classNames;
        }else{
            return getAllFilesBelow(new File(libFile + packageName),".class");
        }
    }

    public static String calcMD5(File file) {
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static Map<String,InputStream> getInternalClassesFromJar(Predicate<String> nameChecker) throws IOException {

        String libFile = getLibFileLocation();
        Map<String,InputStream> classNames = new HashMap<>();
        if (libFile.endsWith(".jar")) {
            JarFile jarFile;
            jarFile = new JarFile(libFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (nameChecker.test(name)){
                    classNames.put(name,jarFile.getInputStream(entry));
                }
            }
            return classNames;
        }else{
            return new HashMap<>();
        }
    }

    public static File createJar(String name) throws IOException {
        final File jarFile = new File(new File(getLibFileLocation()).getParentFile(),name + ".jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile),manifest);
        out.flush();
        out.close();
        return jarFile;
    }

    public static Map<String,InputStream> getAllFilesBelow(File dir,String ext) throws FileNotFoundException {
        Map<String,InputStream> files = new HashMap<>();
        if (dir.isDirectory()) {
            File[] files1 = dir.listFiles();
            if (files1 != null) {
                for (File listFile :files1) {
                    if (listFile.isFile() && listFile.getName().endsWith(ext)) {
                        files.put(listFile.getAbsolutePath().replace(dir.getAbsolutePath(),""),new FileInputStream(listFile));
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

    public static <R> R safeMake(DangerConsumer<R> maker,R defaultResult){
        try {
            return maker.dangerGet();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return defaultResult;
    }

    public static void logInfoConsole(String msg){
        System.out.println(format.format(new Date()) + " [INFO] " + msg);
    }

    public static boolean isJavaType(String name) {
        return !name.endsWith(";");
    }

    public interface DangerConsumer<R>  {
        R dangerGet() throws Throwable;
    }
}
