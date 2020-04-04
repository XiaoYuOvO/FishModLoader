package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.asm.Transformer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Utils {
    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 2147483639;

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

    public static String getLibFileLocation(){
        return URLDecoder.decode(Transformer.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    public static String getLibFileLocationRawURL(){
        return Transformer.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    }


    public static Map<String,InputStream> getInternalTransformerClasses(String packageName) throws IOException {

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

    public static boolean isJavaType(String name) {
        return !name.endsWith(";");
    }
}
