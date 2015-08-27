package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.api.events.UserPendingContactRequestEvent;
import xyz.gghost.jskype.exception.BadResponseException;
import xyz.gghost.jskype.exception.NoPendingContactsException;
import xyz.gghost.jskype.internal.packet.packets.GetContactsPacket;
import xyz.gghost.jskype.var.User;

import java.util.ArrayList;

public class PendingContactEventThread extends Thread {

    private LocalAccount acc;
    private SkypeAPI api;
    private boolean firstTime = true;
    private ArrayList<String> lastUsers = new ArrayList<String>();

    public PendingContactEventThread(LocalAccount acc, SkypeAPI api) {
        this.acc = acc;
        this.api = api;
    }
    @Override
    public void run() {
        while(this.isAlive()){
            try {
                ArrayList<User> newRequests = acc.getContactRequests();
                if (!firstTime) {
                    ArrayList<String> newLastUsers = new ArrayList<String>(); //allows other clients to accept the request
                    for (User user : newRequests) {
                        if(!lastUsers.contains(user.getUsername())){
                            api.getEventManager().executeEvent(new UserPendingContactRequestEvent(user.getUsername()));
                        }
                        newLastUsers.add(user.getUsername());
                    }
                    lastUsers = newLastUsers;
                }else{
                    for (User user : newRequests)
                        lastUsers.add(user.getUsername());
                }
            } catch (NoPendingContactsException e){
            } catch (BadResponseException e){
                System.out.println("ATTENTION: FAILED TO GET PENDING CONTACTS DUE TO 500 / NULL DATA");
                System.out.println("MAY TAKE AN HOUR TO RECOVER - SKYPE BUG");
                System.out.println("RESTART WOULD FIX THIS BUT IT'S NOT CONFIRMED TO DO SO");
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000 * 10);
            } catch (InterruptedException e) {}
            firstTime = false;
        }
    }
}
