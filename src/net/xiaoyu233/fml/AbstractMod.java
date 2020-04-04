package net.xiaoyu233.fml;

public abstract class AbstractMod {
    public AbstractMod(){}
    public abstract void preInit();
    public void postInit(){};
    public abstract String modId();
    public abstract int modVerNum();
    public abstract String modVerStr();
    public abstract String transformPkg();
}