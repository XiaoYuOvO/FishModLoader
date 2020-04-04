package net.xiaoyu233.fml.classloading;

import net.xiaoyu233.fml.asm.Transformer;
import net.xiaoyu233.fml.util.Utils;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class StaticClassLoader {
    private final File serverFile;
    private final File outputFile;
    private final Transformer transformManager;
    private JarOutputStream output;

    public StaticClassLoader(File serverFile, File outputFile, Transformer transformManager) {
        this.serverFile = serverFile;
        this.outputFile = outputFile;
        this.transformManager = transformManager;
    }

    public void start() {
        try {
            JarFile serverJar = new JarFile(this.serverFile);
            Throwable var2 = null;

            try {
                JarOutputStream output = new JarOutputStream(new FileOutputStream(this.outputFile));
                Throwable var4 = null;

                try {
                    this.output = output;
                    Enumeration<JarEntry> entries = serverJar.entries();

                    while(entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.isDirectory()) {
                            output.putNextEntry(new JarEntry(entry.getName()));
                        } else {
                            InputStream source = serverJar.getInputStream(entry);
                            Throwable var8 = null;

                            try {
                                String fileName = entry.getName();
                                if (!fileName.endsWith(".class")) {
                                    output.putNextEntry(new JarEntry(entry.getName()));
                                    Utils.copy(source, output);
                                } else {
                                    byte[] bytes = Utils.readAllBytes(source);
                                    bytes = this.transformManager.transform(bytes);
                                    ClassReader reader = new ClassReader(bytes);
                                    output.putNextEntry(new JarEntry(reader.getClassName() + ".class"));
                                    output.write(bytes);
                                }
                            } catch (Throwable var60) {
                                var8 = var60;
                                throw var60;
                            } finally {
                                if (source != null) {
                                    if (var8 != null) {
                                        try {
                                            source.close();
                                        } catch (Throwable var59) {
                                            var8.addSuppressed(var59);
                                        }
                                    } else {
                                        source.close();
                                    }
                                }

                            }
                        }
                    }
                } catch (Throwable var62) {
                    var4 = var62;
                    throw var62;
                } finally {
                    if (output != null) {
                        if (var4 != null) {
                            try {
                                output.close();
                            } catch (Throwable var58) {
                                var4.addSuppressed(var58);
                            }
                        } else {
                            output.close();
                        }
                    }

                }
            } catch (Throwable var64) {
                var2 = var64;
                throw var64;
            } finally {
                if (serverJar != null) {
                    if (var2 != null) {
                        try {
                            serverJar.close();
                        } catch (Throwable var57) {
                            var2.addSuppressed(var57);
                        }
                    } else {
                        serverJar.close();
                    }
                }

            }
        } catch (Exception var66) {
            var66.printStackTrace();
        }

    }

    public void define(String name, byte[] b, int off, int len) {
        try {
            this.output.putNextEntry(new JarEntry(name.replace(".", "/").concat(".class")));
            this.output.write(b, off, len);
        } catch (IOException ignored) {
        }

    }
}
