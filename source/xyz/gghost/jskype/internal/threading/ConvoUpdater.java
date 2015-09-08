package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.Skype;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.exception.AccountUnusableForRecentException;
import xyz.gghost.jskype.internal.packet.packets.GetConvos;
import xyz.gghost.jskype.var.Conversation;

import java.util.ArrayList;

public class ConvoUpdater extends Thread{
    private Skype acc;
    private SkypeAPI api;
    private boolean first = true;
    private boolean groupFail = false;

    public ConvoUpdater(Skype acc, SkypeAPI api) {
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
                if (first)
                    groupFail = true;
            } catch (NullPointerException e){
              if (api.isDebugMode())
                  e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
            first = false;
        }
    }
}
