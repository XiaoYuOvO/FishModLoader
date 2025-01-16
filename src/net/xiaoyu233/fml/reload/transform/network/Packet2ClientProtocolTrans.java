package net.xiaoyu233.fml.reload.transform.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.Minecraft;
import net.minecraft.NetHandler;
import net.minecraft.Packet;
import net.minecraft.Packet2ClientProtocol;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.network.FMLClientProtocol;
import net.xiaoyu233.fml.util.RemoteModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(Packet2ClientProtocol.class)
public abstract class Packet2ClientProtocolTrans extends Packet implements FMLClientProtocol {
    @Shadow
    private String username;
    private JsonArray modInfos;
    private List<String> signatures;

    @Shadow
    public int getPacketSize() {
        return 0;
    }

    @SuppressWarnings("unused")
    //Used on login check
    public List<RemoteModInfo> getModInfos() throws VersionParsingException {
        return RemoteModInfo.readFromJson(modInfos);
    }

    @Shadow
    public void processPacket(NetHandler connection) {

    }

    @SuppressWarnings("unused")
    //Used on login check
    public List<String> getSignatures() {
        return signatures;
    }

    @Inject(method = "<init>(ILjava/lang/String;Ljava/lang/String;I)V",at = @At(value = "RETURN"))
    public void injectCtor(int par1, String par2Str, String par3Str, int par4, CallbackInfo callbackInfo){
        this.username = par2Str + ":" + "1.6.4" + ":" + "R" + Minecraft.MITE_release_number;
        this.modInfos = (JsonArray) FishModLoader.getModsJson();
        this.signatures = new ArrayList<>();
        this.signatures.add("FishModLoader");
    }

    @Inject(method = "<init>()V", at = @At("RETURN"))
    private void injectCtor(CallbackInfo callbackInfo){
        this.signatures = new ArrayList<>();
    }

    @Inject(method = "readPacketData", at = @At("RETURN"))
    public void readPacketData(DataInput dataInput, CallbackInfo callbackInfo) throws IOException {
        this.modInfos = (JsonArray) new JsonParser().parse(readString(dataInput,32767));
        int i1 = dataInput.readInt();
        for (int i = 0; i < i1; i++) {
            this.signatures.add(readString(dataInput,255));
        }
    }

    @Inject(method = "writePacketData", at = @At("RETURN"))
    public void injectWritePacketData(DataOutput dataOutput, CallbackInfo callbackInfo) throws IOException {
        writeString(modInfos.toString(),dataOutput);
        dataOutput.writeInt(this.signatures.size());
        for (String signature : this.signatures) {
            writeString(signature,dataOutput);
        }
    }
}
