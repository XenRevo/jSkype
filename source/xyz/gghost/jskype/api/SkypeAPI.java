package xyz.gghost.jskype.api;

import lombok.Getter;
import xyz.gghost.jskype.api.command.CommandManager;
import xyz.gghost.jskype.api.events.EventManager;
import xyz.gghost.jskype.exception.FailedToGetProfileException;
import xyz.gghost.jskype.internal.packet.packets.GetProfilePacket;
import xyz.gghost.jskype.internal.threading.*;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;
import java.util.UUID;

public class SkypeAPI {

    public String cookies = "";
    @Getter UUID uuid = UUID.randomUUID();
    @Getter public LocalAccount skype;
    private ArrayList<Group> knownGroups = new ArrayList<Group>();
    @Getter private EventManager eventManager = new EventManager(this);
    @Getter private CommandManager commandManager = new CommandManager();
    private Thread pinger;
    @Getter private Poller poller;
    private Thread contactUpdater;
    private ConvoUpdater convoUpdater;
    private PendingContactEventThread contactThread;

    public SkypeAPI(String email, String user, String pass) {
        this.skype = new LocalAccount(email, user, pass, this);
        init();
    }

    public SkypeAPI(String user, String pass, boolean multithread) {
        this.skype = new LocalAccount(user, pass, this);
        if (multithread) {
            new Thread() {
                @Override
                public void run(){
                    init();
                }
            }.start();
        }else{
            init();
        }
    }
    public SkypeAPI(String user, String pass) {
        this.skype = new LocalAccount(user, pass, this);
        init();
    }
    public SkypeAPI(String email, String user, String pass, boolean multithread) {
        this.skype = new LocalAccount(email, user, pass, this);
        if (multithread) {
            new Thread() {
                @Override
                public void run(){
                    init();
                }
            }.start();
        }else{
            init();
        }
    }

    private void init() {
        //ORDER IS FOR A REASON
        pinger = new Ping(this);
        pinger.start();
        contactUpdater = new ContactUpdater(this.skype, this);
        contactUpdater.start();
        contactThread = new PendingContactEventThread(this.skype, this);
        contactThread.start();
        poller = new Poller(this, skype);
        poller.start();
        convoUpdater = new ConvoUpdater(this.skype, this);
        convoUpdater.start();

    }

    /**
     * Get User object from skype name
     */
    public User getUserFromName(String username) throws FailedToGetProfileException {
        User user = skype.getContact(username);
        return user != null ? user : new GetProfilePacket(this, skype).getUser(username);
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
        convoUpdater.stop();
        contactThread.stop();
    }


}
