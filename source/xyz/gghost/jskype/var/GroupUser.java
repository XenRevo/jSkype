package xyz.gghost.jskype.var;

import lombok.Data;

@Data
public class GroupUser {
    private Role role = Role.USER;
    private User account;

    public GroupUser(User user) {
        account = user;
    }
}
