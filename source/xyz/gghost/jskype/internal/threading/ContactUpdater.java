package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.api.events.UserJoinEvent;
import xyz.gghost.jskype.api.events.UserLeaveEvent;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.internal.packet.packets.GetContactsPacket;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.internal.packet.packets.GetProfilePacket;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.GroupUser;

import java.util.ArrayList;

public class ContactUpdater extends Thread {

    private LocalAccount acc;
    private SkypeAPI api;

    public ContactUpdater(LocalAccount acc, SkypeAPI api) {
        this.acc = acc;
        this.api = api;
    }
    @Override
    public void run() {
        while(this.isAlive()){
            //Update contacts
            try {
                acc.setContactCache(new GetContactsPacket(api, acc).getContacts());
            } catch (Exception e) {}
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
    }
}
