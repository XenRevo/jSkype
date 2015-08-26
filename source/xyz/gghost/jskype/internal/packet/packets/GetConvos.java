package xyz.gghost.jskype.internal.packet.packets;

import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.chat.Chat;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.internal.packet.PacketBuilder;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.var.*;

import java.util.ArrayList;

public class GetConvos {
    private SkypeAPI api;
    private LocalAccount usr;

    public GetConvos(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }

    public ArrayList<Conversation> getRecentChats() throws AccountUnusableForRecentException {
        try {
            ArrayList<Conversation> groups = new ArrayList<Conversation>();
            PacketBuilder options = new PacketBuilder(api);
            options.setUrl("https://client-s.gateway.messenger.live.com/v1/users/ME/conversations?startTime=0&pageSize=200&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
            options.setData("");
            options.setType(RequestType.OPTIONS);
            options.makeRequest(usr);
            //PacketBuilder/builder bug - can't reuse same instance
            PacketBuilder packet = new PacketBuilder(api);
            packet.setUrl("https://client-s.gateway.messenger.live.com/v1/users/ME/conversations?startTime=0&pageSize=200&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
            packet.setData("");
            packet.setType(RequestType.GET);
            String data = packet.makeRequest(usr);
            if (data == null || data.equals(""))
                throw new AccountUnusableForRecentException();
            JSONArray jsonArray = new JSONObject(data).getJSONArray("conversations");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject recent = jsonArray.getJSONObject(i);
                if (recent.getString("targetLink").contains("/contacts/8:")){
                    Conversation theChat = new Conversation(api, recent.getString("id").split("8:")[1], false);
                    groups.add(theChat);
                }else {
                    String id = recent.getString("id").split(":")[1].split("@")[0];
                    Group group = new Group(id, "", null);
                    group = this.setTopicAndPic(recent.getString("id"), group);
                    PacketBuilder members = new PacketBuilder(api);
                    members.setUrl("https://db3-client-s.gateway.messenger.live.com/v1/threads/" + recent.getString("id") + "?startTime=143335&pageSize=100&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
                    members.setType(RequestType.GET);
                    ArrayList<GroupUser> groupMembers = new ArrayList<GroupUser>();
                    JSONArray membersArray = new JSONObject(members.makeRequest(usr)).getJSONArray("members");
                    for (int ii = 0; ii < membersArray.length(); ii++) {
                        JSONObject member = membersArray.getJSONObject(ii);
                        try {
                            Role role = Role.USER;
                            User ussr = usr.getSimpleUser(member.getString("id").replace("8:", ""));
                            if (!member.getString("role").equals("User"))
                                role = Role.ADMIN;
                            GroupUser gu = new GroupUser(ussr);
                            gu.setRole(role);
                            groupMembers.add(gu);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                    group.setConnectedClients(groupMembers);
                    Conversation theChat = new Conversation(api, group.getChatId(), true);
                    theChat.setForcedGroup(true);
                    theChat.setForcedGroupGroup(group);
                    groups.add(theChat);
                }
            }
            return groups;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Group setTopicAndPic(String id, Group group){
        PacketBuilder packet = new PacketBuilder(api);
        packet.setType(RequestType.GET);
        packet.setUrl("https://client-s.gateway.messenger.live.com/v1/threads/" + id +"?view=msnp24Equivalent");
        String data = packet.makeRequest(usr);
        if (data == null)
             return group;
        JSONObject o = new JSONObject(data).getJSONObject("properties");
        if (!o.isNull("topic"))
            group.setTopic(o.getString("topic"));

        if (!o.isNull("picture"))
            group.setPictureUrl(o.getString("picture").split("@")[1]);

        group.setTopic(Chat.decodeText(group.getTopic()));
        return group;
    }
}
