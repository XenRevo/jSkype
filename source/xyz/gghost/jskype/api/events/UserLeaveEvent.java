package xyz.gghost.jskype.api.events;

import lombok.Data;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.GroupUser;
import xyz.gghost.jskype.var.User;

@Data
public class UserLeaveEvent extends Event {
    private User user;
    private Group group;

    public UserLeaveEvent(Group group, User user) {
        this.user = user;
        this.group = group;
    }

}
