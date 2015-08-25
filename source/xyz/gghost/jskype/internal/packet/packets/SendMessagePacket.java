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
    public Message sendPing(Group group, Message msg) {
        String id = String.valueOf(System.currentTimeMillis());
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/19:" + group.getChatId() + "@thread.skype/messages";
        msg.setSender(acc);
        msg.setUpdateUrl(url);
        msg.setTime(id);
        msg.setId(id);

        BasePacket packet = new BasePacket(api);
        packet.setType(RequestType.POST);

        String data = "{\"clientmessageid\":\"" + id + "\",\"originalarrivaltime\":\"2015-08-25T20:44:14.013Z\",\"messagetype\":\"RichText/UriObject\",\"isactive\":true,\"type\":\"Message\",\"content\":\"<URIObject type=\\\"Picture.1\\\" uri=\\\"https://api.asm.skype.com/v1/objects/0-weu-d2-d3e32413367759fca8c0d72be862cf12\\\" url_thumbnail=\\\"https://api.asm.skype.com/v1/objects/0-weu-d2-d3e32413367759fca8c0d72be862cf12/views/imgt1\\\">To view this shared photo, go to: <a href=\\\"https://api.asm.skype.com/s/i?0-weu-d2-d3e32413367759fca8c0d72be862cf12\\\">https://api.asm.skype.com/s/i?0-weu-d2-d3e32413367759fca8c0d72be862cf12<\\/a><OriginalName v=\\\"DealWithIt-500x330.jpg\\\"/><meta type=\\\"photo\\\" originalName=\\\"DealWithIt-500x330.jpg\\\"/><\\/URIObject>\",\"imdisplayname\":\"Ghost\",\"ackrequired\":\"https://db3-client-s.gateway.messenger.live.com/v1/users/ME/conversations/ALL/messages/1440535454883/ack\",\"conversationLink\":\"https://db3-client-s.gateway.messenger.live.com/v1/users/ME/conversations/19:3000ebdcfcca4b42b9f6964f4066e1ad@thread.skype\",\"composetime\":\"2015-08-25T20:44:14.013Z\",\"from\":\"https://db3-client-s.gateway.messenger.live.com/v1/users/ME/contacts/8:gghosted\",\"threadtopic\":\"gr52 chat for fucken wit bots n shit &apos;\",\"id\":\"1440535454883\"}\n";
        packet.setData(data);
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
