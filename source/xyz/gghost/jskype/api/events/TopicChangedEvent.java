package xyz.gghost.jskype.api.events;

import lombok.Data;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;

@Data
public class TopicChangedEvent extends Event {
    private String topic;
    private Group group;
    private User user;
    public TopicChangedEvent(Group group, User user, String topic, String oldTopic) {
        this.topic = topic;
        this.group = group;
        this.oldTopic = oldTopic;
        this.user = user;
    }
    private String oldTopic;
}
