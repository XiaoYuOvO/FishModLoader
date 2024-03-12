package net.xiaoyu233.fml.reload.transform.id_extend;

import net.minecraft.Chunk;
import net.minecraft.Packet97MultiBlockChange;
import net.minecraft.PacketComponentBytes;
import net.minecraft.World;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Packet97MultiBlockChange.class)
public class PacketMultiBlockChangeMixin {
    @Shadow private PacketComponentBytes bytes;

//    @ModifyConstant(method = "<init>(II[SILnet/minecraft/World;)V", constant = @Constant(intValue = 5))
//    private int modifyBlockInterval(int org){
//        return 6;
//    }
//
//    @ModifyConstant(method = "<init>(II[SILnet/minecraft/World;)V", constant = @Constant(intValue = 4))
//    private int modifyMetadataOffset(int org){
//        return 5;
//    }
//
//    @Redirect(method = "<init>(II[SILnet/minecraft/World;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/Chunk;getBlockID(III)I"))
//    private int modifyBlockIdShift(Chunk chunk, int x, int y, int z){
//        return chunk.getBlockID(x, y, z) >> 8;
//    }

    @Inject(method = "<init>(II[SILnet/minecraft/World;)V", at = @At("RETURN"), cancellable = true)
    public void injectHead(int chunk_x, int chunk_z, short[] local_coords, int num_blocks, World world, CallbackInfo callbackInfo) {
        Chunk chunk = world.getChunkFromChunkCoords(chunk_x, chunk_z);
        byte[] bytes = new byte[num_blocks * 6];
        for (int i = 0; i < num_blocks; ++i)
        {
            int offset = i * 6;
            int x = local_coords[i] >> 12 & 15;
            int y = local_coords[i] & 255;
            int z = local_coords[i] >> 8 & 15;
            int block_id = chunk.getBlockID(x, y, z) >> 8;
            int id_extra = (chunk.getBlockID(x, y, z) - block_id * 256);
            int metadata = chunk.getBlockMetadata(x, y, z);
            bytes[offset] = (byte)x;
            bytes[offset + 1] = (byte)y;
            bytes[offset + 2] = (byte)z;
            bytes[offset + 3] = (byte)block_id;
            bytes[offset + 4] = (byte)id_extra;
            bytes[offset + 5] = (byte)metadata;
        }
        this.bytes = new PacketComponentBytes(bytes, ReflectHelper.dyCast(Packet97MultiBlockChange.class,this));
        callbackInfo.cancel();
    }
}
