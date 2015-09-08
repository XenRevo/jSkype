package xyz.gghost.jskype.api.events;

import lombok.Getter;
import xyz.gghost.jskype.api.event.Event;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.User;

@Getter
public class UserJoinEvent extends Event {
    private final User user;
    private final Conversation group;

    public UserJoinEvent(Conversation group, User user) {
        this.user = user;
        this.group = group;
    }
}
