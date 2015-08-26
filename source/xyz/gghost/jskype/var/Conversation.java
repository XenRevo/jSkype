package xyz.gghost.jskype.var;

import lombok.Getter;
import lombok.Setter;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.PingPrepPacket;
import xyz.gghost.jskype.internal.packet.packets.SendMessagePacket;

import java.util.ArrayList;

/**
 * Created by Ghost on 22/08/2015.
 */
public class Conversation extends  Group{
    @Getter @Setter private boolean userChat;
    @Getter @Setter private String id;
    private SkypeAPI api;
    /** Do not use*/
    @Setter @Getter private Group forcedGroupGroup;
    @Getter @Setter private boolean forcedGroup = false;
    @Getter @Setter private ArrayList<GroupUser> connectedClients = new ArrayList<GroupUser>();
    @Getter @Setter private String topic = "";

    /** This class allows backwards and forwards compatibility of the User and Group class.*/

    public Conversation(SkypeAPI api, String id, boolean isGroup) {
        super(id, "", null);
        try {
            if (isGroup) {
                this.setConnectedClients(api.getSkype().getGroupById(id).getConnectedClients());
            }
        }catch(NullPointerException e){
            //jSkype is still loading
        }
        this.userChat = !isGroup;
        this.id = id;
        this.api = api;
    }

    public String getTopic(){
        if (!userChat){
            return getGroup().getTopic();
        }
        return "";
    }
    public Message sendImageToChat(SkypeAPI api, String URL){
        if (!userChat) {
            return new SendMessagePacket(api, api.getSkype()).sendPing(getGroup(), new Message("hi"), new PingPrepPacket(api).urlToId(URL, id));
        } else {
            return new SendMessagePacket(api, api.getSkype()).sendPing(id, new Message("hi"), new PingPrepPacket(api).urlToId(URL, id));
        }
    }
    public Message sendMessage(SkypeAPI api, String text) {
        if (!userChat) {
            return new SendMessagePacket(api, api.getSkype()).sendMessage(new Group(id, "", null), new Message(text));
        } else {
            return new SendMessagePacket(api, api.getSkype()).sendMessage(id, new Message(text));
        }
    }
    public Message sendMessage(SkypeAPI api, Message text) {
        if (!userChat) {
            return new SendMessagePacket(api, api.getSkype()).sendMessage(new Group(id, "", null), text);
        } else {
            return new SendMessagePacket(api, api.getSkype()).sendMessage(id, text);
        }
    }
    public void setForcedGroupGroup(Group forcedGroupGroup){
        this.forcedGroupGroup = forcedGroupGroup;
    }
    /** If chat is a group, get group or return null*/
    public Group getGroup() {
        if(forcedGroup){
            return forcedGroupGroup;
        }

        return api.getSkype().getGroupById(id);
    }
    /** If chat is a user, get user or return null*/
    public User getUser() {
        for (User user : api.getSkype().getContacts()) {
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
        GroupUser user = new GroupUser(api.getSkype().getUserByUsername(username));
        user.setRole(Role.ADMIN);
        return user;
    }
    /**Gets connected clients in the group or user with user chat*/
    public ArrayList<GroupUser> getConnectedClients(){
        if(!userChat){
            return getGroup().getConnectedClients();
        }else{
            ArrayList<GroupUser> users = new ArrayList<GroupUser>();
            users.add(new GroupUser(api.getSkype().getContact(id)));
            users.add(new GroupUser(api.getSkype()));
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
