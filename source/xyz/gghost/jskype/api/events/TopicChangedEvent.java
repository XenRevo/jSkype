package xyz.gghost.jskype.api.events;

import lombok.Getter;
import xyz.gghost.jskype.api.event.Event;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.User;

@Getter
public class TopicChangedEvent extends Event {
    private final String topic;
    private final Conversation group;
    private final User user;
    public TopicChangedEvent(Conversation group, User user, String topic, String oldTopic) {
        this.topic = topic;
        this.group = group;
        this.oldTopic = oldTopic;
        this.user = user;
    }
    private String oldTopic;
}
