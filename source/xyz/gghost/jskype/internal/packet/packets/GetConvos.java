package xyz.gghost.jskype.internal.packet.packets;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.GroupUser;
import xyz.gghost.jskype.var.Role;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;

public class GetConvos {
    private SkypeAPI api;
    private LocalAccount usr;

    public GetConvos(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }

    public ArrayList<Group> getRecent() throws AccountUnusableForRecentException {
        ArrayList<Group> groups = new ArrayList<Group>();
        BasePacket options = new BasePacket(api);
        options.setUrl("https://client-s.gateway.messenger.live.com/v1/users/ME/conversations?startTime=0&pageSize=200&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
        options.setData("");
        options.setType(RequestType.OPTIONS);
        options.makeRequest(usr);
        //BasePacket/builder bug - can't reuse same instance
        BasePacket packet = new BasePacket(api);
        packet.setUrl("https://client-s.gateway.messenger.live.com/v1/users/ME/conversations?startTime=0&pageSize=200&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
        packet.setData("");
        packet.setType(RequestType.GET);
        String data = packet.makeRequest(usr);
        if (data == null || data.equals(""))
            throw new AccountUnusableForRecentException();
        JSONArray jsonArray = new JSONObject(data).getJSONArray("conversations");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject recent = jsonArray.getJSONObject(i);
            if (recent.getString("targetLink").contains("/contacts/8:"))
                continue;
            String id = recent.getString("id").split(":")[1].split("@")[0];
            String topic = recent.isNull("recent") ? "" : recent.getJSONObject("threadProperties").getString("topic");
            topic = StringEscapeUtils.unescapeHtml4(topic);
            BasePacket members = new BasePacket(api);
            members.setUrl("https://db3-client-s.gateway.messenger.live.com/v1/threads/" + recent.getString("id") + "?startTime=143335&pageSize=100&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
            members.setType(RequestType.GET);
            ArrayList<GroupUser> groupMembers = new ArrayList<GroupUser>();
            JSONArray membersArray = new JSONObject(members.makeRequest(usr)).getJSONArray("members");
            for (int ii = 0; ii < membersArray.length(); ii++) {
                JSONObject member = membersArray.getJSONObject(ii);
                try {
                    Role role = Role.USER;
                    User ussr = new User(member.getString("id").split(":")[1]);
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
            Group group = new Group(id, topic, groupMembers);
            groups.add(group);
        }
        return groups;
    }

}
