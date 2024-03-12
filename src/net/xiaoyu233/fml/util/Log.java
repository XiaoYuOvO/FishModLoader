package net.xiaoyu233.fml.util;

import net.xiaoyu233.fml.FishModLoader;

public class Log {

    public static void info(String message){
        FishModLoader.LOGGER.info(message);
    }

    public static void info(String message, Object... args){
        FishModLoader.LOGGER.info(message, args);
    }

    public static void warn(String msg, Throwable t) {
        FishModLoader.LOGGER.warn(msg, t);
    }


    public static void warn(String msg, Object... args) {
        FishModLoader.LOGGER.warn(msg, args);
    }

    public static void debug(String s, Exception e) {
        FishModLoader.LOGGER.debug(s, e);
    }

    public static void warn(String msg) {
        FishModLoader.LOGGER.warn(msg);
    }
}
