package net.xiaoyu233.fml.reload.transform.network;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.Connection;
import net.minecraft.Packet;
import net.minecraft.Packet2Handshake;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.RemoteModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(Packet2Handshake.class)
public abstract class Packet2ClientProtocolTrans extends Packet {
    @Shadow
    private String MC_version;
    @Shadow
    private String MITE_release_number;
    @Shadow
    private int protocolVersion;
    @Shadow
    private String serverHost;
    @Shadow
    private int serverPort;
    @Shadow
    private String username;
    private JsonArray modInfos;
    private final List<String> signatures = new ArrayList<>();

    public Packet2ClientProtocolTrans(int par1, String par2Str, String par3Str, int par4) {
        this.protocolVersion = par1;
        this.serverHost = par3Str;
        this.serverPort = par4;
    }

    @Shadow
    public int getPacketSize() {
        return 0;
    }

    public ArrayList<RemoteModInfo> getModInfos() {
        return Lists.newArrayList(new Gson().fromJson(modInfos, RemoteModInfo[].class));
    }

    @Shadow
    public void processPacket(Connection connection) {

    }

    public List<String> getSignatures() {
        return signatures;
    }

    @Inject(method = "<init>(ILjava/lang/String;Ljava/lang/String;I)V",at = @At(value = "RETURN"))
    public void injectCtor(int par1, String par2Str, String par3Str, int par4, CallbackInfo callbackInfo){
        this.username = par2Str + ":" + "1.6.4" + ":" + "R" + 196;
        this.modInfos = (JsonArray) FishModLoader.getModsJson();
        this.signatures.add("FishModLoader");
    }

    @Override
    @Overwrite
    public void readPacketData(DataInput dataInput) throws IOException {
        this.protocolVersion = dataInput.readInt();
        this.username = readString(dataInput, 36);
        this.serverHost = readString(dataInput, 255);
        this.serverPort = dataInput.readInt();
        String[] parts = this.username.split("\\:");
        if (parts.length > 1) {
            this.MC_version = parts[1];
        }

        if (parts.length > 2) {
            this.MITE_release_number = parts[2];
        }

        this.modInfos = (JsonArray) new JsonParser().parse(readString(dataInput,32767));
        int i1 = dataInput.readInt();
        for (int i = 0; i < i1; i++) {
            this.signatures.add(readString(dataInput,255));
        }
    }

    @Overwrite
    @Override
    public void writePacketData(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.protocolVersion);
        writeString(this.username, dataOutput);
        writeString(this.serverHost, dataOutput);
        dataOutput.writeInt(this.serverPort);
        writeString(modInfos.toString(),dataOutput);
        dataOutput.writeInt(this.signatures.size());
        for (String signature : this.signatures) {
            writeString(signature,dataOutput);
        }
    }
}
