package ru.mail.park.mechanics.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import ru.mail.park.mechanics.GameSession;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.RemotePointService;

import java.util.*;

/**
 * Created by Solovyev on 08/11/2016.
 */
@Service
public class GameSessionService {
    @NotNull
    private final Map<Id<UserProfile>, GameSession> usersMap = new HashMap<>();
    @NotNull
    private final Set<GameSession> gameSessions = new LinkedHashSet<>();

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final GameInitService gameInitService;

    public GameSessionService(@NotNull RemotePointService remotePointService, @NotNull GameInitService gameInitService) {
        this.remotePointService = remotePointService;
        this.gameInitService = gameInitService;
    }

    public Set<GameSession> getSessions() {
        return gameSessions;
    }

    @Nullable
    public GameSession getSessionForUser(@NotNull Id<UserProfile> userId) {
        return usersMap.get(userId);
    }

    public boolean isPlaying(@NotNull Id<UserProfile> userId) {
        return usersMap.containsKey(userId);
    }

    public void notifyGameIsOver(@NotNull GameSession gameSession) {
        final boolean exists = gameSessions.remove(gameSession);
        usersMap.remove(gameSession.getFirst().getId());
        usersMap.remove(gameSession.getSecond().getId());
        if (exists) {
            remotePointService.cutDownConnection(gameSession.getFirst().getId(), CloseStatus.SERVER_ERROR);
            remotePointService.cutDownConnection(gameSession.getSecond().getId(), CloseStatus.SERVER_ERROR);
        }
    }

    public GameSession startGame(@NotNull UserProfile first, @NotNull UserProfile second) {
        final GameSession gameSession = new GameSession(first, second);
        gameSessions.add(gameSession);
        usersMap.put(gameSession.getFirst().getId(), gameSession);
        usersMap.put(gameSession.getSecond().getId(), gameSession);
        gameInitService.initGameFor(gameSession);
        return gameSession;
    }

}
