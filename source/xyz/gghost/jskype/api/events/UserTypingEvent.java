package xyz.gghost.jskype.api.events;

import lombok.Getter;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.GroupUser;
import xyz.gghost.jskype.var.User;

public class UserTypingEvent extends Event {
    @Getter
    private User user;
    private Group group;

    public UserTypingEvent(Group group, User user) {
        this.user = user;
        this.group = group;
    }
}
