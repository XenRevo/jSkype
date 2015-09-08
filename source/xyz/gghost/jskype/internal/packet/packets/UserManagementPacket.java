package xyz.gghost.jskype.internal.packet.packets;

import xyz.gghost.jskype.api.Skype;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.chat.Chat;
import xyz.gghost.jskype.internal.packet.PacketBuilder;
import xyz.gghost.jskype.internal.packet.RequestType;

public class UserManagementPacket {

    private SkypeAPI api;
    private Skype usr;

    public UserManagementPacket(SkypeAPI api, Skype usr) {
        this.api = api;
        this.usr = usr;
    }

    /**
     * @return true = done / false = no perm
     */
    public boolean kickUser(String groupId, String username) {
        //TODO: remove... debug mode stuff
        if (username.equals("gghosted") || username.equals("notghostbot")){
            usr.getGroupById(groupId).sendMessage(api, Chat.bold("jSkype debug mode> ") + "Can't kick Ghost whilst you're running test BETA versions of jSkype");
            usr.getGroupById(groupId).sendMessage(api, Chat.bold("jSkype debug mode> ") + "Reason: attempting to fix the permission kick bug");
            return false;
        }
        PacketBuilder packet = new PacketBuilder(api);
        packet.setUrl("https://client-s.gateway.messenger.live.com/v1/threads/19:" + groupId + "@thread.skype/members/8:" + username);
        packet.setType(RequestType.DELETE);
        return packet.makeRequest(usr) != null;
    }

    /**
     * @return true = done / false = no perm
     */
    public boolean addUser(String groupId, String username) {
        PacketBuilder packet = new PacketBuilder(api);
        packet.setUrl("https://client-s.gateway.messenger.live.com/v1/threads/19:" + groupId + "@thread.skype/members/8:" + username);
        packet.setData("{\"role\":\"User\"}");
        packet.setType(RequestType.PUT);
        return packet.makeRequest(usr) != null;
    }
}
