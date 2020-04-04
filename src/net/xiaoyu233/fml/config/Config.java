package net.xiaoyu233.fml.config;

public interface Config {
    <T> T get(String var1);

    void set(String var1, Object var2);

    void save();

    void load();
}
