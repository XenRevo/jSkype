package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.api.events.UserJoinEvent;
import xyz.gghost.jskype.api.events.UserLeaveEvent;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.internal.packet.packets.GetProfilePacket;
import xyz.gghost.jskype.var.Conversation;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.GroupUser;

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
                    ArrayList<Conversation> sss = new ArrayList<Conversation>();
                    ArrayList<Conversation> newList = new GetConvos(api, acc).getRecentChats();

                    if (newList != null) {
                        acc.setRecentCache(newList);
                        for (Conversation newGroup : newList) {
                            boolean flag = true;
                            for (Conversation oldGr : acc.getConversations()) {
                                if (oldGr.getId().equals(newGroup.getId()))
                                    flag = false;
                            }
                            if (flag)
                                sss.add(newGroup);
                        }
                        acc.getConversations().addAll(sss);
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
