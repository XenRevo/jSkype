package xyz.gghost.jskype.api.events;

import lombok.Data;
import lombok.Getter;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.User;
@Getter
public class UserImagePingEvent extends Event {
    private final User user;
    private final String imageUrl;
    private final Conversation chat;

    public UserImagePingEvent(Conversation group, User user, String imageUrl) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.chat = group;
    }
}
