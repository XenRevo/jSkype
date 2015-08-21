package xyz.gghost.jskype.internal.threading;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.api.events.UserChatEvent;
import xyz.gghost.jskype.api.events.UserJoinEvent;
import xyz.gghost.jskype.api.events.UserLeaveEvent;
import xyz.gghost.jskype.api.events.UserTypingEvent;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.internal.packet.packets.GetProfilePacket;
import xyz.gghost.jskype.var.*;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class Poller extends Thread {

    private SkypeAPI api;
    private LocalAccount usr;
    private String url;
    private String endpoint;
    private BasePacket packet;

    /**
     * This class contains extremely hackky and shitty code - either leave this class or stfu
     * You have been warned
     */

    public Poller(SkypeAPI api, LocalAccount usr) {
        this.api = api;
        this.usr = usr;
    }

    @Override
    public void run() {
        prepare();
        while (this.isAlive()) {
            poll();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }

    public void poll() {
        BasePacket poll = new BasePacket(api);
        poll.setType(RequestType.POST);
        poll.setUrl("https://" + url + "/v1/users/ME/endpoints/SELF/subscriptions/0/poll");
        poll.setData(" ");
        String data = poll.makeRequest(usr);
        if (data == null || data.equals("") || data.equals("{}"))
            return;

        JSONObject messagesAsJson = new JSONObject(data);
        JSONArray json = messagesAsJson.getJSONArray("eventMessages");
        for (int i = 0; i < json.length(); i++) {
            JSONObject object = json.getJSONObject(i);
            try {
                //TODO: switch instead of if
                if (object.getString("resourceType").equals("ThreadUpdate")) {
                    String idShort = object.getString("resourceLink").split("/19:")[1].split("@thread")[0];
                    Group group = null;
                    for (Group groupp : api.getUser().getGroups()) {
                        if (groupp.getChatId().equals(idShort))
                            group = groupp;
                    }
                    ArrayList<String> newUsers = new ArrayList<String>();
                    ArrayList<String> oldUsers = new ArrayList<String>();
                    String left = null;
                    String joined = null;
                    JSONArray members = object.getJSONObject("resource").getJSONArray("members");
                    for (int ii = 0; ii < members.length(); ii++) {
                        JSONObject member = members.getJSONObject(ii);
                        newUsers.add(member.getString("id").split("8:")[1]);
                    }
                    for (GroupUser userr : group.getConnectedClients())
                        oldUsers.add(userr.getAccount().getUsername());
                    boolean done = false;
                    for (String s : oldUsers) {
                        if (!newUsers.contains(s)) {
                            left = s;
                            //ConcurrentModificationException is known to happen for #getConnectedClients... Try again if fails
                            while (!done) {
                                try {
                                    for (GroupUser ussss : group.getConnectedClients()) {
                                        done = true;
                                        if (ussss.getAccount().getUsername().equals(s))
                                            group.getConnectedClients().remove(ussss);
                                    }
                                }catch(ConcurrentModificationException e){}
                            }
                        }
                    }
                    for (String s : newUsers) {
                        if (!oldUsers.contains(s)) {
                            joined = s;
                            group.add(api, s);
                        }
                    }
                    if (joined != null) {
                        //System.out.println(joined + " has joined a chat");
                        api.getEventManager().executeEvent(new UserJoinEvent(group, new GetProfilePacket(api, usr).getUser(joined)));
                    } else if (left != null) {
                        //System.out.println(left + " has left a chat");
                        api.getEventManager().executeEvent(new UserLeaveEvent(group, new GetProfilePacket(api, usr).getUser(left)));
                    }

                } else if (object.getString("resourceType").equals("NewMessage")) {
                    String idShort = object.getString("resourceLink").split("conversations/19:")[1].split("@thread")[0];

                    Group group = null;
                    addGroupToRecent(object);
                    for (Group groupp : api.getUser().getGroups()) {
                        if (groupp.getChatId().equals(idShort))
                            group = groupp;
                    }


                    try {
                        if (object.getJSONObject("resource").getString("messagetype").equals("Control/Typing")){
                            User us = new GetProfilePacket(api, usr).getUser(object.getJSONObject("resource").getString("from").split("8:")[1]);
                            api.getEventManager().executeEvent(new UserTypingEvent(group, us));
                            return;
                        }
                    } catch (Exception e) {}


                    User user = new GetProfilePacket(api, usr).getUser(object.getJSONObject("resource").getString("from").split("s/8:")[1]);
                    user.setDisplayName(StringEscapeUtils.unescapeHtml4(object.getJSONObject("resource").getString("imdisplayname")));

                    Message message = new Message();

                    String content;
                    try {
                        content = object.getJSONObject("resource").getString("content");
                        if (content.contains("Edited previous message:")) {
                            content = content.replaceFirst("Edited previous message: ", "").split("<e_m")[0];
                            message.setEdited(true);
                        }
                    } catch (Exception e) {
                        content = "";
                    }

                    content = StringEscapeUtils.unescapeHtml4(content);
                    content = StringEscapeUtils.unescapeHtml3(content);
                    //TODO: more html unescaping

                    message.setMessage(content);
                    message.setTime(object.getJSONObject("resource").getString("originalarrivaltime"));
                    message.setSender(user);
                    message.setUpdateUrl(object.getString("resourceLink").split("/messages/")[0] + "/messages");
                    //JSONObject doesnt have a .contains and isNull doesn't check for keys - only values
                    try {
                        message.setId(object.getJSONObject("resource").getString("clientmessageid"));
                    } catch (Exception e) {
                        message.setId(object.getJSONObject("resource").getString("skypeeditedid"));
                    }

                    try {
                        api.getCommandManager().runCommand(message, group);
                        api.getEventManager().executeEvent(new UserChatEvent(group, user, message));
                    }catch(Exception e){}
                }


            } catch (ArrayIndexOutOfBoundsException e) {

            } catch (Exception e) {
                System.out.println("Failed to process data from skype.\nMessage: " + object + "\nError: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void addGroupToRecent(JSONObject object) {
        String idLong = object.getString("resourceLink").split("conversations/")[1].split("/")[0];
        String idShort = object.getString("resourceLink").split("conversations/19:")[1].split("@thread")[0];
        //get if already exists
        for (Group group : api.getUser().getGroups()) {
            if (group.getChatId().equals(idShort)) {
                return;
            }
        }
        //get info about group
        BasePacket packett = new BasePacket(api);
        packett.setUrl("https://db3-client-s.gateway.messenger.live.com/v1/threads/" + idLong + "?startTime=143335&pageSize=100&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
        packett.setType(RequestType.GET);
        String data = packett.makeRequest(usr);
        if (data == null)
            return;
        JSONObject recent = new JSONObject(data);

        Group group = new Group(idShort, "", null);
        group = new GetConvos(api, usr).setTopicAndPic(idLong, group);
        BasePacket members = new BasePacket(api);
        members.setUrl("https://db3-client-s.gateway.messenger.live.com/v1/threads/" + idLong + "?startTime=143335&pageSize=100&view=msnp24Equivalent&targetType=Passport|Skype|Lync|Thread");
        members.setType(RequestType.GET);
        ArrayList<GroupUser> groupMembers = new ArrayList<GroupUser>();
        String dataa = members.makeRequest(usr);
        if (dataa == null)
            return;
        JSONArray membersArray = new JSONObject(dataa).getJSONArray("members");
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
                continue;
            }
        }
        group.setConnectedClients(groupMembers);
        api.getUser().getGroupCache().add(group);

    }

    public void prepare() {
        login();
        if (!reg()) {
            System.out.println("Failed to get update data from skype due to a login error... Attempting to relogin, however this wont work until the auto pinger kicks in.");
            login();
            try {
                Thread.sleep(1750);
                prepare();
            } catch (InterruptedException e) {
            }
        }
        save();
    }


    public void login() {
        url = location().split("://")[1].split("/")[0];
        usr.setRegToken(packet.getCon().getHeaderField("Set-RegistrationToken").split(";")[0]);
        endpoint = packet.getCon().getHeaderField("Set-RegistrationToken").split(";")[2].split("=")[1];
    }

    public boolean save() {
        String id = "{\"id\":\"messagingService\",\"type\":\"EndpointPresenceDoc\",\"selfLink\":\"uri\",\"publicInfo\":{\"capabilities\":\"video|audio\",\"type\":\"1\",\"skypeNameVersion\":\"skype.com\",\"nodeInfo\":\"2\",\"version\":\"2\"},\"privateInfo\":{\"epname\":\"Skype\"}}";
        BasePacket packet = new BasePacket(api);
        packet.setType(RequestType.PUT);
        packet.setData(id);
        packet.setUrl("https://" + url + "/v1/users/ME/endpoints/" + endpoint + "/presenceDocs/messagingService");
        return packet.makeRequest(usr) != null;
    }

    public boolean reg() {
        BasePacket packet = new BasePacket(api);
        String id = "{\"channelType\":\"httpLongPoll\",\"template\":\"raw\",\"interestedResources\":[\"/v1/users/ME/conversations/ALL/properties\",\"/v1/users/ME/conversations/ALL/messages\",\"/v1/users/ME/contacts/ALL\",\"/v1/threads/ALL\"]}";
        packet.setData(id);
        packet.setType(RequestType.POST);
        packet.setUrl("https://" + url + "/v1/users/ME/endpoints/SELF/subscriptions");
        return packet.makeRequest(usr) != null;
    }

    public String location() {
        packet = new BasePacket(api);
        packet.setUrl("https://client-s.gateway.messenger.live.com/v1/users/ME/endpoints");
        packet.setType(RequestType.POST);
        packet.setData("{}");
        String data = packet.makeRequest(usr);
        if (data == null) {
            //I have no fucking clue how to handle this. Crash application may be feasible but then after two days it might crash for no reason :/
            System.out.println("Null on location getter");
            System.exit(-1);
        }
        return packet.getCon().getHeaderField("Location");
    }

}
