package xyz.gghost.jskype.var;


import lombok.Data;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.SendMessagePacket;

@Data
public class User {
    private String displayName;
    private String username;
    private String pictureUrl = "https://swx.cdn.skype.com/assets/v/0.0.213/images/avatars/default-avatar-group_46.png";
    private String mood = "";
    //TODO:
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
        return new SendMessagePacket(api, api.getUser()).sendMessage(this.getUsername(), new Message(message));
    }
    /**
     * Send a message to a person
     */
    public Message sendMessage(SkypeAPI api, Message message) {
        return new SendMessagePacket(api, api.getUser()).sendMessage(this.getUsername(), message);
    }
}
