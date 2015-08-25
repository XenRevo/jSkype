package xyz.gghost.jskype.internal.packet.packets;

import xyz.gghost.jskype.api.SkypeAPI;
import xyz.gghost.jskype.internal.packet.BasePacket;
import xyz.gghost.jskype.internal.packet.RequestType;

public class PingPacket {
    private SkypeAPI api;

    public PingPacket(SkypeAPI api) {
        this.api = api;

    }

    public void doNow() {
        BasePacket ping = new BasePacket(api);
        ping.setType(RequestType.POST);
        ping.setUrl("https://web.skype.com/api/v1/session-ping");
        ping.setData("sessionId=" + api.getUuid().toString());
        ping.setIsForm(true);
        String data = ping.makeRequest(api.getSkype());
        if (data == null || data.equals("---")) {
            System.out.println("Skype login expired... Reconnecting");
            api.getSkype().relog();
            api.getPoller().prepare();
        }

    }

}
