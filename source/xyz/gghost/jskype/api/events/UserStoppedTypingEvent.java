package xyz.gghost.jskype.api.events;

import lombok.Data;
import lombok.Getter;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;
@Deprecated
/**
 * Event not inuse - careful when using UserTypingEvent
 */
@Getter
public class UserStoppedTypingEvent extends Event {

    private User user;
    private Group group;

    public UserStoppedTypingEvent(Group group, User user) {
        this.user = user;
        this.group = group;
    }
}
