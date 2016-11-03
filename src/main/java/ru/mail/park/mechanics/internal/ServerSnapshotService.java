package ru.mail.park.mechanics.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mail.park.mechanics.GameSession;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.mechanics.base.ServerPlayerSnap;
import ru.mail.park.mechanics.base.ServerSnap;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Solovyev on 03/11/2016.
 */
@Service
public class ServerSnapshotService {
    @NotNull
    private RemotePointService remotePointService;

    @NotNull
    private ObjectMapper objectMapper = new ObjectMapper();

    public ServerSnapshotService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void sendSnapshotsFor(@NotNull GameSession gameSession, long frameTime) {
        final List<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        final List<ServerPlayerSnap> playersSnaps = new ArrayList<>();
        for (GameUser player : players) {
            playersSnaps.add(player.generateSnap());
        }
        final ServerSnap snap = new ServerSnap();

        snap.setPlayers(playersSnaps);
        snap.setServerFrameTime(frameTime);
        try {
            final Message message = new Message(ServerSnap.class.getName(), objectMapper.writeValueAsString(snap));
            for (GameUser player : players) {
                remotePointService.sendMessageToUser(player.getId(), message);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed sending snapshot", ex);
        }

    }
}
