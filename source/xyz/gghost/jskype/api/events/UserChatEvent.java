package xyz.gghost.jskype.api.events;

import lombok.Data;
import lombok.Getter;
import xyz.gghost.jskype.api.event.Event;
import xyz.gghost.jskype.var.*;

@Getter
public class UserChatEvent extends Event {
    private final User user;
    private final Message msg;
    private final Conversation chat;
    public UserChatEvent(Conversation group, User user, Message msg) {
        this.user = user;
        this.msg = msg;
        chat = group;
    }

    public boolean isEdited(){
        return msg.isEdited();
    }
}
