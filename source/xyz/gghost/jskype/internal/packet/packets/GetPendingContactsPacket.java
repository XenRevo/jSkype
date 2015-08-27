package xyz.gghost.jskype.internal.packet.packets;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.exception.BadResponseException;
import xyz.gghost.jskype.exception.NoPendingContactsException;
import xyz.gghost.jskype.internal.packet.PacketBuilder;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.var.User;

import java.net.URLEncoder;
import java.util.ArrayList;

public class GetPendingContactsPacket {

    private SkypeAPI api;
    private LocalAccount usr;

    public GetPendingContactsPacket(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }

    public ArrayList<User> getPending() throws NoPendingContactsException, BadResponseException {
        PacketBuilder packet = new PacketBuilder(api);
        packet.setType(RequestType.GET);
        packet.setUrl("https://api.skype.com/users/self/contacts/auth-request");
        String a = packet.makeRequest(usr);
        if (a == null) {
            //api.getLogger().warning("Failed to get contact requests");
            throw new BadResponseException();
        } else {
            if (a.equals("")) {
                //api.getLogger().info("No contact requests available");
                throw new NoPendingContactsException();
            }
            ArrayList<User> pending = new ArrayList<User>();
            JSONArray json = new JSONArray(a);
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = json.getJSONObject(i);
                pending.add(new GetProfilePacket(api, usr).getUser(object.getString("sender")));
            }
            return pending;
        }

    }

    public void acceptRequest(String user) {
           String URL = "https://api.skype.com/users/self/contacts/auth-request/" + user + "/accept";
        PacketBuilder packet = new PacketBuilder(api);
        packet.setData("");
        packet.setUrl(URL);
        packet.setIsForm(true);
        packet.setType(RequestType.PUT);
        packet.makeRequest(usr);
        // BasePackets can't be reused
        String URL2 = "https://client-s.gateway.messenger.live.com/v1/users/ME/contacts/";
        PacketBuilder packet2 = new PacketBuilder(api);
        //TODO: Find a replacement for json.org that supports json building
        String data  = "{\"contacts\": [";
        boolean first = true;
        for (User usr : api.getSkype().getContacts()){
            data = data + (!first ? "," : "");
            data = data + "{\"id\": \"" + usr.getUsername() + "\"}";
            first = false;
        }
        data = data + (!first ? "," : "");
        data = data + "{\"id\": \"" + user + "\"}";
        data = data  + "]}";packet2.setData(data);
        //end of json hackky code
        packet2.setUrl(URL2);
        packet2.setIsForm(true);
        packet2.setType(RequestType.PUT);
        packet2.makeRequest(usr);
    }

    public void acceptRequest(User usr) {
        acceptRequest(usr.getUsername());
    }

    public void sendRequest(String user){
        sendRequest(user, "Hi, I'd like to add you as a contact. -Sent from jSkypeAPI");
    }
    public void sendRequest(String user, String message){
        String URL = "https://api.skype.com/users/self/contacts/auth-request/" + user;
        PacketBuilder packet = new PacketBuilder(api);
        packet.setData("greeting=" + URLEncoder.encode(message));
        packet.setUrl(URL);
        packet.setIsForm(true);
        packet.setType(RequestType.PUT);
        packet.makeRequest(usr);
    }
}
