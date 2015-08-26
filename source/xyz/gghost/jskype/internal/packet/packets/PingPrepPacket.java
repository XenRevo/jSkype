package xyz.gghost.jskype.internal.packet.packets;

import org.json.JSONObject;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.BasePacketUploader;
import xyz.gghost.jskype.internal.packet.Header;
import xyz.gghost.jskype.internal.packet.RequestType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Created by Ghost on 26/08/2015.
 */
public class PingPrepPacket {
    SkypeAPI api;
    public PingPrepPacket(SkypeAPI api){
        this.api = api;
    }

    public String urlToId(String url, String groupId){
        String id = getId();
        if (id == null) {
            System.out.println("Failed to get id");
            return null;
        }
        if (!allowRead(id, groupId)) {
            System.out.println("Failed to set perms");
            return null;
        }
        if(!writeData(id, url)){
            System.out.println("Failed to set image data");
            return null;
        }
        return id;
    }

    public String getId(){
        BasePacket packet = new BasePacket(api);
        packet.setUrl("https://api.asm.skype.com/v1/objects");
        packet.setData(" ");
        packet.setSendLoginHeaders(false);
        packet.addHeader(new Header("Authorization", "skype_token " + api.getSkype().getXSkypeToken()));
        packet.setType(RequestType.POST);
        String data = packet.makeRequest(api.getSkype());
        if (data == null)
            return null;
        return new JSONObject(data).getString("id");
    }
    public boolean allowRead(String id, String shortId){
        BasePacket packet = new BasePacket(api);
        packet.setUrl("https://api.asm.skype.com/v1/objects/" + id + "/permissions");
        packet.setData("{\"19:" + shortId + "@thread.skype\":[\"read\"]}");
        packet.setSendLoginHeaders(false);
        packet.addHeader(new Header("Authorization", "skype_token " + api.getSkype().getXSkypeToken()));
        packet.setType(RequestType.PUT);
        String data = packet.makeRequest(api.getSkype());
        if (data == null)
            return false;
        return true;
    }
    public boolean writeData(String id, String url){
        try {
            URL image = new URL(url);

            InputStream data = image.openStream();
            BasePacketUploader packet = new BasePacketUploader(api);
            packet.setUrl("https://api.asm.skype.com/v1/objects/" + id + "/content/imgpsh");
            packet.setSendLoginHeaders(false);
            packet.setFile(true);
            packet.addHeader(new Header("Authorization", "skype_token " + api.getSkype().getXSkypeToken()));
            packet.setType(RequestType.PUT);

            String newData = packet.makeRequest(api.getSkype(), data);
            if (data == null)
                return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


}
