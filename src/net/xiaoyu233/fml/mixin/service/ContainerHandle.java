package net.xiaoyu233.fml.mixin.service;

import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class ContainerHandle extends ContainerHandleVirtual {
   public ContainerHandle(String name) {
      super(name);
   }

   public void addResource(String name, Path path) {
      this.add(new ContainerHandle.Resource(name, path));
   }

   public void addResources(List<Entry<String, Path>> resources) {
      Iterator var2 = resources.iterator();

      while(var2.hasNext()) {
         Entry<String, Path> resource = (Entry)var2.next();
         this.addResource(resource.getKey(), resource.getValue());
      }

   }

   public String toString() {
      return String.format("FishModLoader Root Container(%x)", this.hashCode());
   }

   public class Resource extends ContainerHandleURI {
      private final String name;
      private final Path path;

      public Resource(String name, Path path) {
         super(path.toUri());
         this.name = name;
         this.path = path;
      }

      public String getName() {
         return this.name;
      }

      public Path getPath() {
         return this.path;
      }

      public String toString() {
         return String.format("ContainerHandle.Resource(%s:%s)", this.name, this.path);
      }
   }
}
