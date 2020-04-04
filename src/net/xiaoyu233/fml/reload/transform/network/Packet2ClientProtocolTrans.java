package net.xiaoyu233.fml.reload.transform.network;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.Connection;
import net.minecraft.Packet;
import net.minecraft.Packet2Handshake;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.asm.annotations.Link;
import net.xiaoyu233.fml.asm.annotations.Marker;
import net.xiaoyu233.fml.asm.annotations.Transform;
import net.xiaoyu233.fml.util.ModInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

@Transform(Packet2Handshake.class)
public class Packet2ClientProtocolTrans extends Packet {
    @Link
    private String MC_version;
    @Link
    private String MITE_release_number;
    @Link
    private int a;
    @Link
    private String b;
    @Link
    private String c;
    @Link
    private int d;
    @Link
    private JsonArray modInfos;

    public Packet2ClientProtocolTrans(int par1, String par2Str, String par3Str, int par4) {
        this.a = par1;
        this.b = par2Str + ":" + "1.6.4" + ":" + "R" + 196 + ":" + "FishModLoader";
        this.c = par3Str;
        this.d = par4;
        this.modInfos = (JsonArray) FishModLoader.getModsJson();
    }

    @Override
    @Marker
    public int a() {
        return 0;
    }

    @Override
    @Marker
    public void a(Connection connection) {

    }

    @Override
    public void a(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.a);
        a(this.b, dataOutput);
        a(this.c, dataOutput);
        dataOutput.writeInt(this.d);
        a(modInfos.toString(),dataOutput);
    }

    @Override
    public void a(DataInput dataInput) throws IOException {
        this.a = dataInput.readInt();
        this.b = a(dataInput, 36);
        this.c = a(dataInput, 255);
        this.d = dataInput.readInt();
        String[] parts = this.b.split("\\:");
        if (parts.length > 1) {
            this.MC_version = parts[1];
        }

        if (parts.length > 2) {
            this.MITE_release_number = parts[2];
        }

        this.modInfos = (JsonArray) new JsonParser().parse(a(dataInput,32767));
    }

    public ArrayList<ModInfo> getModInfos() {
        return Lists.newArrayList(new Gson().fromJson(modInfos, ModInfo[].class));
    }

    public String getB() {
        return b;
    }
}
