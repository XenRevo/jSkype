package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.exception.FailedToGetContactsException;
import xyz.gghost.jskype.internal.packet.packets.GetContactsPacket;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.var.Group;

import java.util.ArrayList;

public class ContactUpdater extends Thread {

    private LocalAccount acc;
    private SkypeAPI api;
    private boolean groupFail = false;

    public ContactUpdater(LocalAccount acc, SkypeAPI api) {
        this.acc = acc;
        this.api = api;
    }

    @Override
    public void run() {
        while (this.isAlive()) {
            try {
                acc.setContactCache(new GetContactsPacket(api, acc).getContacts());
            } catch (Exception e) {}

            try {
                if (!groupFail) {
                    ArrayList<Group> group = acc.getGroupCache();
                    for (Group g : new GetConvos(api, acc).getRecent()) {
                        boolean flag = false;
                        for (Group testGroup : group) {
                            if (testGroup.getChatId().equals(g.getChatId()))
                                flag = true;
                        }
                        if (!flag)
                            group.add(g);
                    }
                    acc.setGroupCache(group);
                }
            } catch (AccountUnusableForRecentException e) {
                groupFail = true;
            }
            try {
                Thread.sleep(1000 * 60 * 2);
            } catch (InterruptedException e) {
            }
        }
    }
}
