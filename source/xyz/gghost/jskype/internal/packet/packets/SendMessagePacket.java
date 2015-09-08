package xyz.gghost.jskype.internal.packet.packets;


import xyz.gghost.jskype.api.Skype;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.PacketBuilder;
import xyz.gghost.jskype.internal.packet.RequestType;
import xyz.gghost.jskype.internal.impl.Group;
import xyz.gghost.jskype.var.Message;

public class SendMessagePacket {

    private SkypeAPI api;
    private Skype acc;

    public SendMessagePacket(SkypeAPI api, Skype acc) {
        this.api = api;
        this.acc = acc;
    }

    public Message editMessage(Message msg) {
        PacketBuilder packet = new PacketBuilder(api);
        packet.setType(RequestType.POST);
        packet.setData("{\"content\":\"" + msg.getMessage().replace("\"", "\\\"") + "\",\"messagetype\":\"RichText\",\"contenttype\":\"text\",\"skypeeditedid\":\"" + msg.getId() + "\"}");

        packet.setUrl(msg.getUpdateUrl());
        packet.makeRequest(acc);

        return msg;
    }

    public Message sendMessage(Group group, Message msg) {
        String id = String.valueOf(System.currentTimeMillis());
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/19:" + group.getChatId() + "@thread.skype/messages";
        msg.setSender(api.getSkype().getSimpleUser(acc.getUsername()));
        msg.setUpdateUrl(url);
        msg.setTime(id);
        msg.setId(id);

        PacketBuilder packet = new PacketBuilder(api);
        packet.setType(RequestType.POST);
        packet.setData("{\"content\":\"" + msg.getMessage().replace("\"", "\\\"") + "\",\"messagetype\":\"RichText\",\"contenttype\":\"text\",\"clientmessageid\":\"" + id + "\"}");

        packet.setUrl(url);
        packet.makeRequest(acc);

        return msg;
    }
    public Message sendPing(Group group, Message msg, String ids) {
        String id = String.valueOf(System.currentTimeMillis());
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/19:" + group.getChatId() + "@thread.skype/messages";
        msg.setSender(api.getSkype().getSimpleUser(acc.getUsername()));
        msg.setUpdateUrl(url);
        msg.setTime(id);
        msg.setId(id);

        PacketBuilder packet = new PacketBuilder(api);
        packet.setType(RequestType.POST);

        String data  = "{content: \"<URIObject type=\\\"Picture.1\\\" uri=\\\"https://api.asm.skype.com/v1/objects/" + ids + "\\\" url_thumbnail=\\\"https://api.asm.skype.com/v1/objects/"+ ids + "/views/imgt1\\\">To view this shared photo, go to: <a href=\\\"https://api.asm.skype.com/s/i?" + ids + "\\\">https://api.asm.skype.com/s/i?" + ids + "<\\/a><OriginalName v=\\\"^005CFF2010F86CC63570CA528D9B2CCFE3BF3B54DF8A01E92E^pimgpsh_thumbnail_win_distr.jpg\\\"/><meta type=\\\"photo\\\" originalName=\\\"^005CFF2010F86CC63570CA528D9B2CCFE3BF3B54DF8A01E92E^pimgpsh_thumbnail_win_distr.jpg\\\"/><\\/URIObject>\", messagetype: \"RichText/UriObject\", contenttype: \"text\", clientmessageid: \"" + id + "\"}";
        packet.setData(data);
        packet.setUrl(url);
        packet.makeRequest(acc);

        return msg;
    }
    public Message sendPing(String user, Message msg, String ids) {
        String id = String.valueOf(System.currentTimeMillis());
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/8:" + user + "/messages";
        msg.setSender(api.getSkype().getSimpleUser(acc.getUsername()));;
        msg.setUpdateUrl(url);
        msg.setTime(id);
        msg.setId(id);
        PacketBuilder packet = new PacketBuilder(api);
        packet.setType(RequestType.POST);
        String data = "{content: \"<URIObject type=\\\"Picture.1\\\" uri=\\\"https://api.asm.skype.com/v1/objects/" + ids + "\\\" url_thumbnail=\\\"https://api.asm.skype.com/v1/objects/"+ ids + "/views/imgt1\\\">To view this shared photo, go to: <a href=\\\"https://api.asm.skype.com/s/i?" + ids + "\\\">https://api.asm.skype.com/s/i?" + ids + "<\\/a><OriginalName v=\\\"^005CFF2010F86CC63570CA528D9B2CCFE3BF3B54DF8A01E92E^pimgpsh_thumbnail_win_distr.jpg\\\"/><meta type=\\\"photo\\\" originalName=\\\"^005CFF2010F86CC63570CA528D9B2CCFE3BF3B54DF8A01E92E^pimgpsh_thumbnail_win_distr.jpg\\\"/><\\/URIObject>\", messagetype: \"RichText/UriObject\", contenttype: \"text\", clientmessageid: \"" + id + "\"}";
        packet.setData(data);
        packet.setUrl(url);
        packet.makeRequest(acc);

        return msg;
    }

    public Message sendMessage(String user, Message msg) {

        String id = String.valueOf(System.currentTimeMillis());
        String url = "https://client-s.gateway.messenger.live.com/v1/users/ME/conversations/8:" + user.toLowerCase() + "/messages";
        msg.setSender(acc.getSimpleUser(acc.getUsername()));
        msg.setUpdateUrl(url);
        msg.setTime(id);
        msg.setId(id);

        PacketBuilder packet = new PacketBuilder(api);
        packet.setType(RequestType.POST);
        packet.setData("{\"content\":\"" + msg.getMessage().replace("\"", "\\\"") + "\",\"messagetype\":\"RichText\",\"contenttype\":\"text\",\"clientmessageid\":\"" + id + "\"}");

        packet.setUrl(url);
        packet.makeRequest(acc);

        return msg;
    }

}
