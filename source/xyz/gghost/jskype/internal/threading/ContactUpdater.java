package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.Skype;
import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.GetContactsPacket;

public class ContactUpdater extends Thread {

    private Skype acc;
    private SkypeAPI api;

    public ContactUpdater(Skype acc, SkypeAPI api) {
        this.acc = acc;
        this.api = api;
    }
    @Override
    public void run() {
        while(this.isAlive()){
            try {
              new GetContactsPacket(api, acc).setupContact();
            } catch (Exception e) {}
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
    }
}
