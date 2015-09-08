package xyz.gghost.jskype.internal.threading;

import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.packets.PingPacket;

public class Ping extends Thread {
    private PingPacket ping;

    public Ping(SkypeAPI api) {
        ping = new PingPacket(api);
    }

    @Override
    public void run() {
        while (this.isAlive()) {
            ping.doNow();
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {}
        }

    }
}
