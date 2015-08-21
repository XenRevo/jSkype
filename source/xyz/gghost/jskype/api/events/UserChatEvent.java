package xyz.gghost.jskype.api.events;

import lombok.Data;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.GroupUser;
import xyz.gghost.jskype.var.Message;
import xyz.gghost.jskype.var.User;

@Data
public class UserChatEvent extends Event {
    private User user;
    private Message msg;
    private Group group;

    public UserChatEvent(Group group, User user, Message msg) {
        this.user = user;
        this.msg = msg;
        this.group = group;
    }
}
