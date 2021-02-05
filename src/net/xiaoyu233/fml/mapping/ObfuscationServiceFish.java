package net.xiaoyu233.fml.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import org.spongepowered.tools.obfuscation.interfaces.IMixinAnnotationProcessor;
import org.spongepowered.tools.obfuscation.service.IObfuscationService;
import org.spongepowered.tools.obfuscation.service.ObfuscationTypeDescriptor;

import java.util.Collection;
import java.util.Set;

public class ObfuscationServiceFish implements IObfuscationService {
   public static final String MCP = "mcp";
   public static final String NOTCH = "notch";
   public static final String REOBF_SRG_FILE = "reobfMcpFile";
   public static final String REOBF_EXTRA_SRG_FILES = "reobfMcpFiles";
   public static final String REOBF_NOTCH_FILE = "reobfNotchSrgFile";
   public static final String REOBF_EXTRA_NOTCH_FILES = "reobfNotchSrgFiles";
   public static final String OUT_SRG_SRG_FILE = "outSrgFile";
   public static final String OUT_NOTCH_SRG_FILE = "outNotchSrgFile";

   public Set<String> getSupportedOptions() {
      return ImmutableSet.of("reobfMcpFile", "reobfMcpFiles", "reobfNotchSrgFile", "reobfNotchSrgFiles", "outSrgFile", "outNotchSrgFile");
   }

   public Collection<ObfuscationTypeDescriptor> getObfuscationTypes(IMixinAnnotationProcessor ap) {
      Builder<ObfuscationTypeDescriptor> list = ImmutableList.builder();
      if (!ap.getOptions("mappingTypes").contains("tsrg")) {
         list.add(new ObfuscationTypeDescriptor("mcp", "reobfMcpFile", "reobfMcpFiles", "outSrgFile", ObfuscationEnvironmentFish.class));
      }

      list.add(new ObfuscationTypeDescriptor("notch", "reobfNotchSrgFile", "reobfNotchSrgFiles", "outNotchSrgFile", ObfuscationEnvironmentFish.class));
      return list.build();
   }
}
