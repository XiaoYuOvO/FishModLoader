package net.xiaoyu233.fml.reload.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class MojangAPI {
    private static final String UUID_URL = "https://api.mojang.com/profiles/minecraft";
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final HashMap<String, MojangUUID> cacheUUID = new HashMap<>();
    private static final HashMap<String, MojangProfile> cacheProfile = new HashMap<>();

    public static String fixImageUrl(String imageUrl) {
        String ret = imageUrl;
        String[] coll;
        String[] coll2;
        String playername;
        MojangUUID u;
        MojangProfile p;
        if (imageUrl.startsWith("http://skins.minecraft.net/MinecraftSkins/")) {
            try {
                coll = imageUrl.split("/");
                coll2 = coll[coll.length - 1].split("\\.");
                playername = coll2[0];
                u = MojangAPI.getPlayerUUID(playername);
                if (u == null) {
                    return ret;
                }

                p = MojangAPI.getProfile(u.id);
                if (p == null) {
                    return ret;
                }

                if (p.properties[0].valueBase64.textures.SKIN.metadata.model.equals("slim")) {
                    ret = alexToSteveService(p.properties[0].valueBase64.textures.SKIN.url, playername);
                } else {
                    ret = p.properties[0].valueBase64.textures.SKIN.url;
                }

                ret = ret.isEmpty() ? imageUrl : ret;
            } catch (Exception var9) {
                var9.printStackTrace();
                ret = imageUrl;
            }
        }

        if (imageUrl.startsWith("http://skins.minecraft.net/MinecraftCloaks/")) {
            try {
                coll = imageUrl.split("/");
                coll2 = coll[coll.length - 1].split("\\.");
                playername = coll2[0];
                u = MojangAPI.getPlayerUUID(playername);
                if (u == null) {
                    return ret;
                }

                p = MojangAPI.getProfile(u.id);
                if (p == null) {
                    return ret;
                }

                ret = p.properties[0].valueBase64.textures.CAPE.url;
                ret = ret.isEmpty() ? imageUrl : ret;
            } catch (Exception var8) {
                var8.printStackTrace();
                ret = imageUrl;
            }
        }

        return ret;
    }

    private static String alexToSteveService(String skinurl, String playername) {
        String ret = skinurl;
        String sUrl = "http://www.lumylabs.com/lumyskinpatch/alextosteve.php?skinurl=" + skinurl + "&playername=" + playername + "&v=1.6.4";

        try {
            URL url = new URL(sUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.connect();
            if (connection.getContentType().equals("image/png")) {
                ret = sUrl;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static String readUrlGET(String urlString) throws IOException, InterruptedException {
        String ret;
        HttpURLConnection con = null;

        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            if (con.getResponseCode() == 429) {
                Thread.sleep(30000L);
                con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
            }

            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while(true) {
                String inputLine;
                if ((inputLine = in.readLine()) == null) {
                    ret = response.toString();
                    break;
                }

                response.append(inputLine);
                response.append('\n');
            }
        } finally {
            if (con != null) {
                con.disconnect();
            }

        }

        con.disconnect();

        return ret;
    }

    private static String readUrlPOST(String urlParameters) throws IOException {
        String ret;
        HttpURLConnection con = null;

        try {
            URL url = new URL(UUID_URL);
            con = (HttpURLConnection)url.openConnection();
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(postData);
            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while(true) {
                String inputLine;
                if ((inputLine = in.readLine()) == null) {
                    ret = response.toString();
                    break;
                }

                response.append(inputLine);
                response.append('\n');
            }
        } finally {
            if (con != null) {
                con.disconnect();
            }

        }
        con.disconnect();
        return ret;
    }

    public static MojangUUID getPlayerUUID(String playername) {
        if (cacheUUID.containsKey(playername)) {
            return cacheUUID.get(playername);
        } else {
            try {
                String json = readUrlPOST("[\"" + playername + "\"]");
                Gson gson = new Gson();
                List<MojangUUID> res = gson.fromJson(json, (new TypeToken<List<MojangUUID>>() {
                }).getType());
                if (res.size() == 1) {
                    cacheUUID.put(playername, res.get(0));
                    return res.get(0);
                } else {
                    cacheUUID.put(playername, null);
                    return null;
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                synchronized(cacheUUID) {
                    if (cacheUUID.containsKey(playername)) {
                        return cacheUUID.get(playername);
                    }
                }

                cacheUUID.put(playername, null);
                return null;
            }
        }
    }

    public static MojangProfile getProfile(String uuid) {
        synchronized(cacheProfile) {
            if (cacheProfile.containsKey(uuid)) {
                return cacheProfile.get(uuid);
            }
        }

        try {
            String json = readUrlGET(PROFILE_URL + uuid);
            Gson gson = new Gson();
            MojangProfile res = gson.fromJson(json, MojangProfile.class);
            byte[] decoded = DatatypeConverter.parseBase64Binary(res.properties[0].value);
            String decodedJson = new String(decoded, StandardCharsets.UTF_8);
            res.properties[0].valueBase64 = gson.fromJson(decodedJson, PropertiesValueBase64.class);
            synchronized(cacheProfile) {
                cacheProfile.put(uuid, res);
            }

            return res;
        } catch (Exception var14) {
            var14.printStackTrace();
            synchronized(cacheProfile) {
                if (cacheProfile.containsKey(uuid)) {
                    return cacheProfile.get(uuid);
                }
            }

            synchronized(cacheProfile) {
                cacheProfile.put(uuid, null);
                return null;
            }
        }
    }

    @SuppressWarnings("unused")
    public static class MojangUUID {
        public String id;
        public String name;
        public boolean legacy;
        public boolean demo;

        MojangUUID() {
        }
    }

    public static class MojangProfile {
        public String id;
        public String name;
        public Properties[] properties;

        MojangProfile() {
        }
    }

    @SuppressWarnings("unused")
    public static class Properties {
        public String name;
        public String value;
        public String signature;
        public PropertiesValueBase64 valueBase64;

        Properties() {
        }
    }

    @SuppressWarnings("unused")
    public static class PropertiesValueBase64 {
        long timestamp;
        String profileId;
        String profileName;
        boolean signatureRequired;
        Textures textures = new Textures();

        PropertiesValueBase64() {
        }
    }

    public static class Textures {
        Skin SKIN = new Skin();
        Cape CAPE = new Cape();
    }

    public static class Skin {
        String url = "";
        Metadata metadata = new Metadata();
    }

    public static class Cape {
        String url = "";
    }

    public static class Metadata{
        String model = "";
    }
}
