package xyz.gghost.jskype.api.events;

import lombok.Getter;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;
@Deprecated
/**
 * Event not inuse - careful when using UserTypingEvent
 */
public class UserStoppedTypingEvent extends Event {
    @Getter
    private User user;
    private Group group;

    public UserStoppedTypingEvent(Group group, User user) {
        this.user = user;
        this.group = group;
    }
}
