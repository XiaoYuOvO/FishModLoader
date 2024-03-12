package net.xiaoyu233.fml.network;

import net.fabricmc.loader.api.VersionParsingException;
import net.xiaoyu233.fml.util.RemoteModInfo;

import java.util.List;

public interface FMLClientProtocol {
    List<String> getSignatures();

    List<RemoteModInfo> getModInfos() throws VersionParsingException;
}
