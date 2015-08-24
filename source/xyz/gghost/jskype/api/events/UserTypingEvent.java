package xyz.gghost.jskype.api.events;

import lombok.Getter;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.User;

public class UserTypingEvent extends Event {
    @Getter
    private User user;
    private Conversation chat;

    public UserTypingEvent(Conversation group, User user) {
        this.user = user;
        this.chat = group;
    }
}
