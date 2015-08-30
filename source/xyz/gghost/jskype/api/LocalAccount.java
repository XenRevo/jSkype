package xyz.gghost.jskype.api;

import lombok.Getter;
import lombok.Setter;
import salt.samczsun.SkypeAuthentication;
import salt.samczsun.exception.InvalidCredentialsException;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.exception.BadResponseException;
import xyz.gghost.jskype.exception.NoPendingContactsException;
import xyz.gghost.jskype.internal.packet.packets.GetContactsPacket;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.internal.packet.packets.GetPendingContactsPacket;
import xyz.gghost.jskype.internal.packet.packets.GetProfilePacket;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;


/**
 * original
 * This should be in xyz.gghost.jskype.var, but since it's used for most of the main methods, it's staying in the "root" package
 *
 * @author Ghost
 */
public class LocalAccount extends User {
    private SkypeAPI api;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String username;
    @Getter
    private String password;
    @Getter
    @Setter
    private String xSkypeToken;
    @Getter
    @Setter
    private String regToken;
    @Setter
    private ArrayList<User> contactCache = new ArrayList<User>();
    @Setter
    private ArrayList<Conversation> recentCache = new ArrayList<Conversation>();

    public LocalAccount(String username, String password, SkypeAPI api) {
        this.username = username;
        this.email = username;
        this.password = password;
        this.api = api;
        init();
    }

    public LocalAccount(String email, String username, String password, SkypeAPI api) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.api = api;
        init();
    }

    private void init() {
        System.out.println("API> Logging in");
        relog();
        System.out.println("API> Getting user data");
        System.out.println("API> Getting contacts");
        try {
            new GetContactsPacket(api, this).setupContact();
        } catch (Exception e) {
            System.out.println("API> Failed to get your entire contacts due to a bad account. Try an alt?");
        }
        System.out.println("API> Getting groups, non-contact conversations, group information");
        try {
            recentCache = new GetConvos(api, this).getRecentChats();
        } catch (AccountUnusableForRecentException e) {
            System.out.println("API> Failed to get recent contacts due to a bad account. Try an alt?");
        }
        System.out.println("API> Initialized!");
    }

    /**
     * Login
     */
    public void relog() {

        try {
            new SkypeAuthentication().login(api, this);
        } catch (InvalidCredentialsException e) {
            if (api.displayErrorMessages())
                System.out.println("API> Bad username + password");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            if (api.displayErrorMessages())
                System.out.println("API> Failed to login!");
            System.exit(-1);
        }
    }

    /**
     * Get group by short id (no 19: + @skype blah blah blah)
     */
    public Group getGroupById(String id) {
        for (Conversation group : recentCache) {
            if ((!group.isUserChat()) && group.getId().equals(id))
                return group.getForcedGroupGroup();
        }
        return null;
    }

    /**
     * This method will get as much data as possible about a user without contacting to skype
     */
    public User getSimpleUser(String username) {
        User user = getContact(username);
        return user != null ? user : new User(username);
    }

    /**
     * get user by username
     */
    public User getUserByUsername(String username) {
        User user = getContact(username);
        return user != null ? user : new GetProfilePacket(api, this).getUser(username);
    }

    /**
     * Get contact by username
     */
    public User getContact(String username) {
        for (User contact : getContacts()) {
            if (contact.getUsername().equalsIgnoreCase(username))
                return contact;
        }
        return null;
    }

    /**
     * Now same as #getRecent
     */
    @Deprecated
    public ArrayList<Conversation> getAllChats() {
        return recentCache;
    }

    /**
     * Gets contacts
     */
    public ArrayList<User> getContacts() {
        return contactCache;
    }

    /**
     * Get recent chats - this includes contacts, none-contacts and groups
     */
    public ArrayList<Conversation> getRecent() {
        return recentCache;
    }

    /**
     * Get the known groups ("recent conversations" and active chats).
     */
    public ArrayList<Conversation> getConversations() {
        return recentCache;
    }

    /**
     * Gets pending contact requests
     */
    public ArrayList<User> getContactRequests() throws BadResponseException, NoPendingContactsException {
        return new GetPendingContactsPacket(api, this).getPending();
    }

    /**
     * Attempts to accept a contact request - can take upto 2 minutes to appear as a contact
     */
    public void acceptContact(String username) {
        new GetPendingContactsPacket(api, this).acceptRequest(username);
    }

    /**
     * Attempts to send a contact request
     */
    public void sendContactRequest(String username) {
        new GetPendingContactsPacket(api, this).sendRequest(username);
    }

    /**
     * Attempts to send a contact request with a custom greeting
     */
    public void sendContactRequest(String username, String greeting) {
        new GetPendingContactsPacket(api, this).sendRequest(username, greeting);
    }
    /**
     * Skype db lookup / search
     */

    /**
     * Get info about self // although dis class extends User, it doesn't use the users variables. Call this to get uptodate information
     */

    /**
     * Get if a user is online
     */

}
