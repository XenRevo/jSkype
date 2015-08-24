package xyz.gghost.jskype.var;

import lombok.Getter;
import lombok.Setter;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.SendMessagePacket;

import java.util.ArrayList;

/**
 * Created by Ghost on 22/08/2015.
 */
public class Conversation extends Group{
    @Getter @Setter private boolean userChat;
    @Getter @Setter private String id;
    private SkypeAPI api;
    /** Do not use*/
    @Setter @Getter private Group forcedGroupGroup;
    @Getter @Setter private boolean forcedGroup = false;

    /** This class allows backwards and forwards compatibility of the User and Group class.*/

    public Conversation(SkypeAPI api, String id, boolean isGroup) {

        super(id, "", null);
        if (isGroup){
            this.setConnectedClients(api.getUser().getGroupById(id).getConnectedClients());
        }
        this.userChat = !isGroup;
        this.id = id;
        this.api = api;
    }
    @Override
    public String getTopic(){
        if (!userChat){
            return getGroup().getTopic();
        }
        return "";
    }
    public Message sendMessage(SkypeAPI api, String text) {
        if (!userChat) {
            return new SendMessagePacket(api, api.getUser()).sendMessage(new Group(id, "", null), new Message(text));
        } else {
            return new SendMessagePacket(api, api.getUser()).sendMessage(id, new Message(text));
        }
    }
    public Message sendMessage(SkypeAPI api, Message text) {
        if (!userChat) {
            return new SendMessagePacket(api, api.getUser()).sendMessage(new Group(id, "", null), text);
        } else {
            return new SendMessagePacket(api, api.getUser()).sendMessage(id, text);
        }
    }
    /** If chat is a group, get group or return null*/
    public Group getGroup() {
        if(forcedGroup)
            return forcedGroupGroup;
        return api.getUser().getGroupById(id);
    }
    /** If chat is a user, get user or return null*/
    public User getUser() {
        for (User user : api.getUser().getContacts()) {
            if (user.getUsername().equalsIgnoreCase(id))
                return user;
        }
        return null;
    }
    /** get user by username */
    public GroupUser getUser(String username){
        if(!userChat){
            return getGroup().getUser(username);
        }
        GroupUser user = new GroupUser(api.getUser().getUserByUsername(username));
        user.setRole(Role.ADMIN);
        return user;
    }
    /**Gets connected clients in the group or user with user chat*/
    public ArrayList<GroupUser> getConnectedClients(){
        if(!userChat){
            return getGroup().getConnectedClients();
        }else{
            ArrayList<GroupUser> users = new ArrayList<GroupUser>();
            users.add(new GroupUser(api.getUser().getContact(id)));
            users.add(new GroupUser(api.getUser()));
            return users;
        }
    }
    /** Kick a user from a group - groups only*/
    public void kick(String username){
        if (!userChat)
            getGroup().kick(api, username);
    }
     /**Add a user from a group - groups only*/
    public void add(String username){
        if (!userChat)
            getGroup().add(api, username);
    }
     /** Kick a user from a group - groups only */
    public void kick(User username){
        if (!userChat)
            getGroup().kick(api, username);
    }
    /** Add a user from a group - groups only*/
    public void add(User username){
        if (!userChat)
            getGroup().add(api, username);
    }
    public boolean isAdmin(String user){
        if (!userChat)
            return getGroup().isAdmin(user);
        return true;

    }
    public boolean isAdmin(User user){
        if (!userChat)
            return getGroup().isAdmin(user);
        return true;

    }


}
