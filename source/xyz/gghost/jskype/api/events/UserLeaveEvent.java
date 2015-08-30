package xyz.gghost.jskype.api.events;

import lombok.Data;
import lombok.Getter;
import xyz.gghost.jskype.api.event.Event;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;

@Data
@Getter
public class UserLeaveEvent extends Event {
    private final User user;
    private final Group group;

    public UserLeaveEvent(Group group, User user) {
        this.user = user;
        this.group = group;
    }

}
