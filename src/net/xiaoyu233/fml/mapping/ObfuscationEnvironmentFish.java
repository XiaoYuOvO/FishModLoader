package net.xiaoyu233.fml.mapping;

import org.spongepowered.tools.obfuscation.ObfuscationEnvironment;
import org.spongepowered.tools.obfuscation.ObfuscationType;
import org.spongepowered.tools.obfuscation.mapping.IMappingProvider;
import org.spongepowered.tools.obfuscation.mapping.IMappingWriter;
import org.spongepowered.tools.obfuscation.mapping.mcp.MappingProviderSrg;
import org.spongepowered.tools.obfuscation.mapping.mcp.MappingWriterSrg;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

public class ObfuscationEnvironmentFish extends ObfuscationEnvironment {
   protected ObfuscationEnvironmentFish(ObfuscationType type) {
      super(type);
   }

   protected IMappingProvider getMappingProvider(Messager messager, Filer filer) {
      return new MappingProviderSrg(messager, filer);
   }

   protected IMappingWriter getMappingWriter(Messager messager, Filer filer) {
      return new MappingWriterSrg(messager, filer);
   }
}
