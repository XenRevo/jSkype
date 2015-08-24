package xyz.gghost.jskype.internal.packet.packets;


import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.Message;

public class SendMessagePacket {

    private SkypeAPI api;
    private LocalAccount acc;

    public SendMessagePacket(SkypeAPI api, LocalAccount acc) {
        this.api = api;
        this.acc = acc;
    }

    public Message editMessage(Message msg) {
        BasePacket packet = new BasePacket(api);
        packet.setType(RequestType.POST);
        packet.setData("{\"content\":\"" + msg.getMessage().replace("\"", "\\\"") + "\",\"messagetype\":\"RichText\",\"contenttype\":\"text\",\"skypeeditedid\":\"" + msg.getId() + "\"}");

        packet.setUrl(msg.getUpdateUrl());
        packet.makeRequest(acc);

        return msg;
    }

    public Message sendMessage(Group group, Message msg) {
        String id = String.valueOf(System.currentTimeMillis());
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/19:" + group.getChatId() + "@thread.skype/messages";
        msg.setSender(acc);
        msg.setUpdateUrl(url);
        msg.setTime(id);
        msg.setId(id);

        BasePacket packet = new BasePacket(api);
        packet.setType(RequestType.POST);
        packet.setData("{\"content\":\"" + msg.getMessage().replace("\"", "\\\"") + "\",\"messagetype\":\"RichText\",\"contenttype\":\"text\",\"clientmessageid\":\"" + id + "\"}");

        packet.setUrl(url);
        packet.makeRequest(acc);

        return msg;
    }

    public Message sendMessage(String user, Message msg) {

        String id = String.valueOf(System.currentTimeMillis());
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/8:" + user.toLowerCase() + "/messages";
        msg.setSender(acc);
        msg.setUpdateUrl(url);
        msg.setTime(id);
        msg.setId(id);

        BasePacket packet = new BasePacket(api);
        packet.setType(RequestType.POST);
        packet.setData("{\"content\":\"" + msg.getMessage().replace("\"", "\\\"") + "\",\"messagetype\":\"RichText\",\"contenttype\":\"text\",\"clientmessageid\":\"" + id + "\"}");

        packet.setUrl(url);
        packet.makeRequest(acc);

        return msg;
    }

}
