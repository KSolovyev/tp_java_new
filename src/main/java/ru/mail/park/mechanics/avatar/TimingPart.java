package ru.mail.park.mechanics.avatar;

import ru.mail.park.pinger.TimingData;

/**
 * Created by Solovyev on 01/11/2016.
 */
@SuppressWarnings("unused")
public class TimingPart {
    //All timings are in ms
    private long clientPing = 0;
    private long clientTimeshift = 0;

    public long getClientPing() {
        return clientPing;
    }

    public void setClientPing(long clientPing) {
        this.clientPing = clientPing;
    }

    public long getClientTimeshift() {
        return clientTimeshift;
    }

    public void setClientTimeshift(long clientTimeshift) {
        this.clientTimeshift = clientTimeshift;
    }
}
