package xyz.gghost.jskype.internal.packet.packets;

import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.PacketBuilder;
import xyz.gghost.jskype.internal.packet.RequestType;

public class UserManagementPacket {

    private SkypeAPI api;
    private LocalAccount usr;

    public UserManagementPacket(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }

    /**
     * @return true = done / false = no perm
     */
    public boolean kickUser(String groupId, String username) {
        PacketBuilder packet = new PacketBuilder(api);
        packet.setUrl("https://client-s.gateway.messenger.live.com/v1/threads/19:" + groupId + "@thread.skype/members/8:" + username);
        packet.setType(RequestType.DELETE);
        if (packet.makeRequest(usr) != null)
            return true;
        return false;
    }

    /**
     * @return true = done / false = no perm
     */
    public boolean addUser(String groupId, String username) {
        PacketBuilder packet = new PacketBuilder(api);
        packet.setUrl("https://client-s.gateway.messenger.live.com/v1/threads/19:" + groupId + "@thread.skype/members/8:" + username);
        packet.setData("{\"role\":\"User\"}");
        packet.setType(RequestType.PUT);
        if (packet.makeRequest(usr) != null)
            return true;
        return false;
    }
}
