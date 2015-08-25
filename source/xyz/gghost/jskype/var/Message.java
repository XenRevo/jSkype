package xyz.gghost.jskype.var;


import lombok.Data;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.SendMessagePacket;

@Data
public class Message {

    private User sender;
    private String message;
    private String updateUrl;
    private boolean edited = false;
    private String time;
    private String id;

    public Message(String message) {
        this.message = message;
    }

    public Message() {}


    /**
     * Edit the message
     */
    public Message editMessage(SkypeAPI api, String message){
        setMessage(message);
        edited = true;
        return new SendMessagePacket(api, api.getSkype()).editMessage(this);
    }
    /**
     * Once setMessage has edited the message locally, this will update the edit on skypes servers
     * @param api
     * @return
     */
    public Message updateEdit(SkypeAPI api) {
        edited = true;
        return new SendMessagePacket(api, api.getSkype()).editMessage(this);
    }
}
