package xyz.gghost.jskype.internal.packet.packets;

import org.json.JSONObject;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.var.User;

public class GetProfilePacket {
    private SkypeAPI api;
    private LocalAccount usr;

    public GetProfilePacket(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }

    public User getUser(String username) {
        BasePacket packet = new BasePacket(api);
        packet.setType(RequestType.POST);
        User user = new User();
        packet.setUrl("https://api.skype.com/users/self/contacts/profiles");
        packet.setData("contacts[]=" + username);
        packet.setIsForm(true);

        String data = packet.makeRequest(usr);
        if (data == null) {
            return null;
        }
        data = data.replaceFirst("\\[", "").replace("]", "");
        JSONObject jsonObject = new JSONObject(data);
        // user.setFirstName(jsonObject.getString("firstname"));
        user.setUsername(username);
        user.setPictureUrl(jsonObject.isNull("avatarUrl") ? "https://swx.cdn.skype.com/assets/v/0.0.213/images/avatars/default-avatar-group_46.png" : jsonObject.getString("avatarUrl"));
        user.setDisplayName(jsonObject.isNull("displayname") ? (jsonObject.isNull("firstname") ? username : jsonObject.getString("firstname")) : jsonObject.getString("displayname"));
        user.setMood(jsonObject.isNull("richMood") ? (jsonObject.isNull("mood") ? "" : jsonObject.getString("mood")) : jsonObject.getString("richMood"));
        return user;
    }
}
