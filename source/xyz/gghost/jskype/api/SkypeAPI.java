package xyz.gghost.jskype.api;

import lombok.Getter;
import lombok.Setter;
import xyz.gghost.jskype.api.command.CommandManager;
import xyz.gghost.jskype.api.events.EventListener;
import xyz.gghost.jskype.api.events.EventManager;
import xyz.gghost.jskype.api.events.UserChatEvent;
import xyz.gghost.jskype.exception.FailedToGetProfileException;
import xyz.gghost.jskype.internal.packet.packets.GetProfilePacket;
import xyz.gghost.jskype.internal.threading.ContactUpdater;
import xyz.gghost.jskype.internal.threading.GroupUpdater;
import xyz.gghost.jskype.internal.threading.Ping;
import xyz.gghost.jskype.internal.threading.Poller;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class SkypeAPI {

    public String cookies = "";
    @Getter UUID uuid = UUID.randomUUID();
    @Getter public LocalAccount user;
    private ArrayList<Group> knownGroups = new ArrayList<Group>();
    @Getter private EventManager eventManager = new EventManager(this);
    @Getter private CommandManager commandManager = new CommandManager();
    private Thread pinger;
    @Getter private Poller poller;
    private Thread contactUpdater;
    private GroupUpdater groupUpdater;

    /**
     * @param email Email - If you use an email to login to skype, type your email here, otherwise, use your skype name.
     * @param user  User - Skype name
     * @param pass  Pass - Password
     */
    public SkypeAPI(String email, String user, String pass) {
        this.user = new LocalAccount(email, user, pass, this);
        init();
    }

    /**
     * For those who do not use a microsoft email/pass login for skype
     *
     * @param user
     * @param pass
     */
    public SkypeAPI(String user, String pass) {
        this.user = new LocalAccount(user, pass, this);
        init();
    }

    private void init() {
        pinger = new Ping(this);
        pinger.start();
        poller = new Poller(this, user);
        poller.start();
        contactUpdater = new ContactUpdater(this.user, this);
        contactUpdater.start();
        groupUpdater = new GroupUpdater(this.user, this);
        groupUpdater.start();
    }

    /**
     * Get User object from skype name
     *
     * @param user Username
     * @return Null or User
     */
    public User getUserFromName(String user) throws FailedToGetProfileException {
        User u = new GetProfilePacket(this, this.user).getUser(user);
        if (u != null)
            return u;
        throw new FailedToGetProfileException();
    }

    /**
     * Stop threads related to this skype app.
     * If you don't have some kinda loop, the app will close.
     */
    public void stop() {
        //DO NOT CHANGE FROM stop() to interrupt()
        pinger.stop();
        poller.stop();
        contactUpdater.stop();
        groupUpdater.stop();
    }


}
