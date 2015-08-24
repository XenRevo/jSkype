package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.LocalAccount;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.api.events.UserJoinEvent;
import xyz.gghost.jskype.api.events.UserLeaveEvent;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.internal.packet.packets.GetProfilePacket;
import xyz.gghost.jskype.var.Group;
import xyz.gghost.jskype.var.GroupUser;

import java.util.ArrayList;

/**
 * Created by Ghost on 22/08/2015.
 */
public class GroupUpdater extends Thread{
    private LocalAccount acc;
    private SkypeAPI api;
    private boolean groupFail = false;

    public GroupUpdater(LocalAccount acc, SkypeAPI api) {
        this.acc = acc;
        this.api = api;
    }
    @Override
    public  void run() {
        while (this.isAlive()) {
            try {
                if (!groupFail) {
                    //Allow 200 + recent
                    ArrayList<Group> sss = new ArrayList<Group>();
                    ArrayList<Group> newList = new GetConvos(api, acc).getRecentGroups();
                    if (newList != null) {
                        for (Group newGroup : newList) {
                            boolean flag = true;
                            for (Group oldGr : acc.getGroups()) {
                                if (oldGr.getChatId().equals(newGroup.getChatId()))
                                    flag = false;
                            }
                            if (flag)
                                sss.add(newGroup);

                        }
                        acc.getGroups().addAll(sss);
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
