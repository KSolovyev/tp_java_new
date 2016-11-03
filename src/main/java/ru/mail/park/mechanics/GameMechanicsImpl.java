package ru.mail.park.mechanics;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.mechanics.internal.ClientSnapshotsService;
import ru.mail.park.mechanics.internal.GameInitService;
import ru.mail.park.mechanics.internal.ServerSnapshotService;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;
import ru.mail.park.websocket.RemotePointService;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author k.solovyev
 */
@SuppressWarnings("unused")
@Service
public class GameMechanicsImpl implements GameMechanics {
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanicsImpl.class);

    @NotNull
    private AccountService accountService;

    @NotNull
    private ClientSnapshotsService clientSnapshotsService;

    @NotNull
    private ServerSnapshotService serverSnapshotService;

    @NotNull
    private RemotePointService remotePointService;

    @NotNull
    private GameInitService gameInitService;

    @NotNull
    private Set<Id<UserProfile>> playingUsers = new HashSet<>();

    @NotNull
    private Set<GameSession> allSessions = new LinkedHashSet<>();

    @NotNull
    private ConcurrentLinkedQueue<Id<UserProfile>> waiters = new ConcurrentLinkedQueue<>();

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public GameMechanicsImpl(@NotNull AccountService accountService, @NotNull ServerSnapshotService serverSnapshotService, @NotNull RemotePointService remotePointService, @NotNull GameInitService gameInitService) {
        this.accountService = accountService;
        this.serverSnapshotService = serverSnapshotService;
        this.remotePointService = remotePointService;
        this.gameInitService = gameInitService;
        this.clientSnapshotsService = new ClientSnapshotsService();
    }

    @Override
    public void addClientSnapshot(@NotNull Id<UserProfile> userId, @NotNull ClientSnap clientSnap) {
        tasks.add(() -> clientSnapshotsService.pushClientSnap(userId, clientSnap));
    }

    @Override
    public void addUser(@NotNull Id<UserProfile> user) {
        if (playingUsers.contains(user)) {
            return;
        }
        waiters.add(user);
    }

    private void tryStartGames() {
        final List<UserProfile> matchPlayers = new ArrayList<>();

        while (waiters.size() >= 2 || waiters.size() >= 1 && matchPlayers.size() >= 1) {
            final Id<UserProfile> candidate = waiters.poll();
            if (!insureCandidate(candidate)) {
                continue;
            }
            matchPlayers.add(accountService.getUserById(candidate));
            if(matchPlayers.size() == 2) {
                starGame(matchPlayers.get(0), matchPlayers.get(1));
            }
        }
    }

    private boolean insureCandidate(@NotNull Id<UserProfile> candidate) {
        return remotePointService.isConnected(candidate) &&
                accountService.getUserById(candidate) != null;
    }

    @Override
    public void gmStep(long frameTime) {
        while (!tasks.isEmpty()) {
            final Runnable nextTask = tasks.poll();
            if (nextTask != null) {
                try {
                    nextTask.run();
                } catch (RuntimeException ex) {
                    LOGGER.error("Cant handle game task", ex);
                }
            }
        }

        for (GameSession session : allSessions) {
            clientSnapshotsService.processSnapshotsFor(session);
        }

        //TODO: game objects

        //TODO: server side ping update

        //TODO: Collisions

        final Iterator<GameSession> iterator = allSessions.iterator();
        while (iterator.hasNext()) {
            final GameSession session = iterator.next();
            try {
                serverSnapshotService.sendSnapshotsFor(session, frameTime);
            } catch (RuntimeException ex) {
                LOGGER.error("Failed send snapshots, terminating the session", ex);
                remotePointService.cutDownConnection(session.getFirst().getId(), CloseStatus.SERVER_ERROR);
                remotePointService.cutDownConnection(session.getSecond().getId(), CloseStatus.SERVER_ERROR);
                playingUsers.remove(session.getFirst().getId());
                playingUsers.remove(session.getSecond().getId());
                iterator.remove();
            }
        }

        tryStartGames();
        clientSnapshotsService.clear();
    }

    @Override
    public void reset() {

    }

    private void starGame(@NotNull UserProfile first, @NotNull UserProfile second) {
        final GameSession gameSession = new GameSession(first, second);
        gameInitService.initGameFor(gameSession);
        allSessions.add(gameSession);
    }
}
