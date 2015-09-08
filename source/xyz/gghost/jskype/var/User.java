package xyz.gghost.jskype.var;


import lombok.Data;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.PingPrepPacket;
import xyz.gghost.jskype.internal.packet.packets.SendMessagePacket;

@Data
public class User {
    private String displayName;
    private String username;
    private String pictureUrl = "https://swx.cdn.skype.com/assets/v/0.0.213/images/avatars/default-avatar-group_46.png";
    private String mood = "";
    private boolean isContact = false;

    public User() {}
    public User(String username) {
        displayName = username;
        this.username = username;
    }
    /**
     * Send a message to a person
     */
    public Message sendMessage(SkypeAPI api, String message) {
        return new SendMessagePacket(api, api.getSkype()).sendMessage(this.getUsername(), new Message(message));
    }
    /**
     * Send a message to a person
     */
    public Message sendMessage(SkypeAPI api, Message message) {
        return new SendMessagePacket(api, api.getSkype()).sendMessage(this.getUsername(), message);
    }
    /** Send an image (url - not a gif ) to a chat*/
    public Message sendImageToChat(SkypeAPI api, String URL) {
        return new SendMessagePacket(api, api.getSkype()).sendPing(username, new Message("hi"), new PingPrepPacket(api).urlToId(URL, username, false));
    }
    /** Send an image (url - not a gif ) to a chat*/
    public Message sendImageByIdToChat(SkypeAPI api, String id) {
        return new SendMessagePacket(api, api.getSkype()).sendPing(username, new Message("hi"), id);
    }
}
