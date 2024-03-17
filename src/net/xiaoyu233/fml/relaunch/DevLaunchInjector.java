package net.xiaoyu233.fml.relaunch;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class is decompiled from fabric's DevLaunchInjector
 * **/
public final class DevLaunchInjector {
    public DevLaunchInjector() {
    }

    public static void main(String[] args) throws Throwable {
        String env = System.clearProperty("fml.dli.env");
        String main = System.clearProperty("fml.dli.main");
        String config = System.clearProperty("fml.dli.config");
        if (main == null) {
            System.err.println("error: missing fml.dli.main property, can't launch");
            System.exit(1);
        } else if (env != null && config != null) {
            Path configFile;
            if (Files.isRegularFile(configFile = Paths.get(decodeEscaped(config))) && Files.isReadable(configFile)) {
                List<String> extraArgs = new ArrayList<>();
                Map<String, String> extraProperties = new HashMap<>();

                try {
                    parseConfig(configFile, env, extraArgs, extraProperties);
                    String[] newArgs = extraArgs.toArray(new String[args.length + extraArgs.size()]);
                    System.arraycopy(args, 0, newArgs, extraArgs.size(), args.length);
                    args = newArgs;

                    for (Map.Entry<String, String> stringStringEntry : extraProperties.entrySet()) {
                        System.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
                    }
                } catch (IOException var10) {
                    warnNoop("parsing failed: " + var10);
                }
            } else {
                warnNoop("missing or unreadable config file (" + configFile + ")");
            }
        } else {
            warnNoop("missing fml.dli.env or fml.dli.config properties");
        }

        MethodHandle handle = MethodHandles.publicLookup().findStatic(Class.forName(main), "main", MethodType.methodType(Void.TYPE, String[].class));
        handle.invokeExact(args);
    }

    private static void parseConfig(Path file, String env, List<String> extraArgs, Map<String, String> extraProperties) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file);
        Throwable var9 = null;

        try {
            int state = 0;

            while(true) {
                String line;
                int pos;
                String key;
                label220:
                while(true) {
                    while(true) {
                        boolean indented;
                        do {
                            do {
                                if ((line = reader.readLine()) == null) {
                                    return;
                                }
                            } while(line.isEmpty());

                            indented = line.charAt(0) == ' ' || line.charAt(0) == '\t';
                            line = line.trim();
                        } while(line.isEmpty());

                        if (!indented) {
                            if (line.startsWith("common")) {
                                pos = "common".length();
                                break label220;
                            }

                            if (line.startsWith(env)) {
                                pos = env.length();
                                break label220;
                            }

                            state = 3;
                        } else {
                            if (state == 0) {
                                throw new IOException("value without preceding attribute: " + line);
                            }

                            if (state == 1) {
                                extraArgs.add(line);
                            } else if (state == 2) {
                                pos = line.indexOf(61);
                                key = pos >= 0 ? line.substring(0, pos).trim() : line;
                                String value = pos >= 0 ? line.substring(pos + 1).trim() : "";
                                extraProperties.put(key, value);
                            } else if (state != 3) {
                                throw new IllegalStateException();
                            }
                        }
                    }
                }

                state = switch (line.substring(pos)) {
                    case "Args" -> 1;
                    case "Properties" -> 2;
                    default -> throw new IOException("invalid attribute: " + line);
                };
            }
        } catch (Throwable var23) {
            var9 = var23;
            throw var23;
        } finally {
            if (reader != null) {
                if (var9 != null) {
                    try {
                        reader.close();
                    } catch (Throwable var22) {
                        var9.addSuppressed(var22);
                    }
                } else {
                    reader.close();
                }
            }

        }
    }

    private static void warnNoop(String msg) {
        System.out.printf("warning: dev-launch-injector in pass-through mode, %s%n", msg);
    }

    private static String decodeEscaped(String s) {
        if (!s.contains("@@")) {
            return s;
        } else {
            Matcher matcher = Pattern.compile("@@([0-9a-fA-F]{1,4})").matcher(s);
            StringBuilder ret = new StringBuilder(s.length());

            int start;
            for(start = 0; matcher.find(); start = matcher.end()) {
                ret.append(s, start, matcher.start());
                ret.append((char)Integer.parseInt(matcher.group(1), 16));
            }

            ret.append(s, start, s.length());
            return ret.toString();
        }
    }
}
