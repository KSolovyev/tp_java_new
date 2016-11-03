package ru.mail.park.pinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mail.park.pinger.requests.PingData;
import ru.mail.park.websocket.Message;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Solovyev on 05/04/16.
 */
@Service
public class PingServiceImpl implements PingService {
    private static final long MAX_PING_MILLIS = TimeUnit.SECONDS.toMillis(1L);

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(PingServiceImpl.class);
    @NotNull
    private final AtomicLong idGenerator = new AtomicLong(0);
    @NotNull
    private final Map<Id<PingData.Request>, Long> pendingRequests = new ConcurrentHashMap<>();
    @NotNull
    private final Map<Id<UserProfile>, TimingData> user2timings = new ConcurrentHashMap<>();
    @NotNull
    private final Executor pingUpdater = Executors.newSingleThreadExecutor();
    private final ObjectMapper objectMapper = new ObjectMapper();


    private final RemotePointService remotePointService;

    @Autowired
    public PingServiceImpl(RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    @Override
    public void rememberPing(@NotNull Id<UserProfile> userId, long clientTimestamp, Id<PingData.Request> requestId) {
        final Long timeWas = pendingRequests.get(requestId);
        if (timeWas == null) {
            return;
        }
        final long now = Clock.systemDefaultZone().millis();
        final long ping = now - timeWas;
        if (ping > MAX_PING_MILLIS) {
            return;
        }
        final long clientTimeShift = now - (clientTimestamp + ping / 2);
        user2timings.put(userId, new TimingData(ping, clientTimeShift));
    }

    @Nullable
    @Override
    public TimingData getTimings(@NotNull Id<UserProfile> userId) {
        return user2timings.get(userId);
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    @Override
    public void refreshPing(@NotNull Id<UserProfile> userId) {
        if (!remotePointService.isConnected(userId)) {
            return;
        }
        pingUpdater.execute(() -> {

            final long id = idGenerator.getAndIncrement();
            final long now = Clock.systemDefaultZone().millis();
            final PingData.Request request = PingData.Request.create().id(id).build();
            try {
                final Message pingMessage = new Message(PingData.Request.class.getName(),
                        objectMapper.writeValueAsString(request));

                remotePointService.sendMessageToUser(userId, pingMessage);
            } catch (IOException e) {
                LOGGER.error("Unnable to send ping request to user " + userId, e);
                return;
            }
            pendingRequests.put(Id.of(id), now);
        });
    }

}
