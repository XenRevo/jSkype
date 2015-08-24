package xyz.gghost.jskype.api.events;

import lombok.Data;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.User;

@Data
public class UserImagePingEvent extends Event {
    private User user;
    private String imageUrl;
    private Conversation chat;

    public UserImagePingEvent(Conversation group, User user, String imageUrl) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.chat = group;
    }
}
