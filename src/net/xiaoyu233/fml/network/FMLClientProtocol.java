package net.xiaoyu233.fml.network;

import net.xiaoyu233.fml.util.RemoteModInfo;

import java.util.ArrayList;
import java.util.List;

public interface FMLClientProtocol {
    public abstract List<String> getSignatures();

    public abstract ArrayList<RemoteModInfo> getModInfos();
}
