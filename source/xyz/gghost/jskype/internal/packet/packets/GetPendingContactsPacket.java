package xyz.gghost.jskype.internal.packet.packets;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.exception.BadResponseException;
import xyz.gghost.jskype.exception.NoPendingContactsException;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;

public class GetPendingContactsPacket {

    private SkypeAPI api;
    private LocalAccount usr;

    public GetPendingContactsPacket(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }

    public ArrayList<User> getPending() throws NoPendingContactsException, BadResponseException {
        BasePacket packet = new BasePacket(api);
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
        BasePacket packet = new BasePacket(api);
        packet.setData("");
        packet.setUrl(URL);
        packet.setIsForm(true);
        packet.setType(RequestType.OPTIONS);
        packet.makeRequest(usr);
        packet.setType(RequestType.PUT);
        packet.makeRequest(usr);
    }

    public void acceptRequest(User usr) {
        acceptRequest(usr.getUsername());
    }

}
