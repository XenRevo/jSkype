package xyz.gghost.jskype.api.events;

import lombok.Data;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.User;

@Data
public class UserOtherFilesPingEvent extends Event {
    private User user;
    private Conversation chat;

    public UserOtherFilesPingEvent(Conversation group, User user) {
        this.user = user;
        this.chat = group;
    }
}
