package xyz.gghost.jskype.api;

import lombok.Getter;
import lombok.Setter;
import salt.samczsun.SkypeAuthentication;
import salt.samczsun.exception.InvalidCredentialsException;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.exception.BadResponseException;
import xyz.gghost.jskype.exception.FailedToGetContactsException;
import xyz.gghost.jskype.exception.NoPendingContactsException;
import xyz.gghost.jskype.internal.packet.packets.GetContactsPacket;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.internal.packet.packets.GetPendingContactsPacket;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;


/**
 * This should be in xyz.gghost.jskype.var, but since it's used for most of the main methods, it's staying in the "root" package
 *
 * @author Ghost
 */
public class LocalAccount extends User {
    private SkypeAPI api;
    @Getter @Setter private String email;
    @Getter @Setter private String username;
    @Getter private String password;
    @Getter @Setter private String xSkypeToken;
    @Getter @Setter private String regToken;
    @Setter private ArrayList<User> contactCache;
    @Setter @Getter private ArrayList<Group> groupCache = new ArrayList<Group>();

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
        System.out.println("Logging in");
        relog();
        System.out.println("Getting user data");
        try {
            contactCache = new GetContactsPacket(api, this).getContacts();
        } catch (Exception e) {
            System.out.println("Failed to get your entire contacts due to a bad account. Try an alt?");
        }
        try {
            groupCache = new GetConvos(api, this).getRecent();
        } catch (AccountUnusableForRecentException e) {
            System.out.println("Failed to get recent contacts due to a bad account. Try an alt?");
        }
    }
    /** Login */
    public void relog() {

        try {
            new SkypeAuthentication().login(api, this);
        } catch (InvalidCredentialsException e) {
            System.out.println("Bad username + password");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to login!");
            System.exit(-1);
        }
    }

    /**
     * Gets contacts
     */
    public ArrayList<User> getContacts() {
        return contactCache;
    }

    /**
     * Get the known groups ("recent conversations" and active chats)
     * This gets groups only - not contacts
     */
    public ArrayList<Group> getGroups() {
        return groupCache;
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
}
