package xyz.gghost.jskype.api.events;

import lombok.Data;
import lombok.Getter;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;

@Getter
public class TopicChangedEvent extends Event {
    private final String topic;
    private final Group group;
    private final User user;
    public TopicChangedEvent(Group group, User user, String topic, String oldTopic) {
        this.topic = topic;
        this.group = group;
        this.oldTopic = oldTopic;
        this.user = user;
    }
    private String oldTopic;
}
