package ru.mail.park.pinger;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.pinger.requests.PingData;
import ru.mail.park.websocket.Message;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

/**
 * Created by Solovyev on 05/04/16.
 */
public interface PingService {

    void rememberPing(@NotNull Id<UserProfile> userName, long clientTimestamp, Id<PingData.Request> requestId);

    TimingData getTimings(@NotNull Id<UserProfile> userName);

    void refreshPing(@NotNull Id<UserProfile> userName);

}
