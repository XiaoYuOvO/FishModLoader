package net.xiaoyu233.fml.modfixer;

import net.fabricmc.loader.impl.discovery.ModCandidate;
import net.fabricmc.loader.impl.metadata.LoaderModMetadata;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class LegacyModCandidate extends ModCandidate {
    protected LegacyModCandidate(List<Path> paths, String localPath, long hash, LoaderModMetadata metadata, boolean requiresRemap, Collection<ModCandidate> nestedMods) {
        super(paths, localPath, hash, metadata, requiresRemap, nestedMods);
    }
}
