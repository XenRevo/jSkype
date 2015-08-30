package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.var.Conversation;

import java.util.ArrayList;

/**
 * Created by Ghost on 22/08/2015.
 */
public class ConvoUpdater extends Thread{
    private LocalAccount acc;
    private SkypeAPI api;
    private boolean groupFail = false;

    public ConvoUpdater(LocalAccount acc, SkypeAPI api) {
        this.acc = acc;
        this.api = api;
    }
    @Override
    public  void run() {
        while (this.isAlive()) {
            try {
                if (!groupFail) {
                    //Allow 200 + recent
                    ArrayList<Conversation> newList = new GetConvos(api, acc).getRecentChats();
                    if (newList != null) {
                        acc.setRecentCache(newList);
                        for (Conversation newGroup : newList) {
                            boolean flag = true;
                            for (Conversation oldGr : acc.getRecent()) {
                                if (oldGr.getId().equals(newGroup.getId()))
                                    flag = false;
                            }
                            if (flag)
                                acc.getRecent().add(newGroup);
                        }
                    }
                }
            } catch (AccountUnusableForRecentException e) {
                groupFail = true;
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
    }
}
