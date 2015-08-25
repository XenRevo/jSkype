package xyz.gghost.jskype.api.events;

import lombok.Data;

/**
 * Created by Ghost on 25/08/2015.
 */
@Data
public class UserPendingContactRequestEvent {
    String user;
    public UserPendingContactRequestEvent(String user){
        this.user = user;
    }
}
