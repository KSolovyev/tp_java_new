package ru.mail.park.mechanics.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import ru.mail.park.mechanics.Config;
import ru.mail.park.mechanics.GameSession;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.mechanics.avatar.PositionPart;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.ServerPlayerSnap;
import ru.mail.park.mechanics.requests.InitGame;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.RemotePointService;

import java.io.IOException;
import java.util.*;

/**
 * Created by Solovyev on 03/11/2016.
 */
@Service
public class GameInitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSnapshotService.class);

    @NotNull
    private final RemotePointService remotePointService;
    @NotNull
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameInitService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void initGameFor(@NotNull GameSession gameSession) {
        gameSession.getFirst().getSquare().claimPart(PositionPart.class).setBody(new Coords(0.0f, 0.0f));
        gameSession.getSecond().getSquare().claimPart(PositionPart.class).setBody(
                new Coords(Config.PLAYGROUND_WIDTH - Config.SQUARE_SIZE,
                        Config.PLAYGROUND_HEIGHT - Config.SQUARE_SIZE)
        );
        final Collection<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        for (GameUser player : players) {
            final InitGame.Request initMessage = createInitMessageFor(gameSession, player.getId());
            //noinspection OverlyBroadCatchBlock
            try {
                final Message message = new Message(InitGame.Request.class.getName(),
                        objectMapper.writeValueAsString(initMessage));
                remotePointService.sendMessageToUser(player.getId(), message);
            } catch (IOException e) {
                //TODO: Reentrance mechanism
                players.forEach(playerToCutOff -> remotePointService.cutDownConnection(playerToCutOff.getId(),
                        CloseStatus.SERVER_ERROR));
                LOGGER.error("Unnable to start a game", e);
            }
        }
    }

    @SuppressWarnings("TooBroadScope")
    private InitGame.Request createInitMessageFor(@NotNull GameSession gameSession, @NotNull Id<UserProfile> userId) {
        final GameUser self = gameSession.getSelf(userId);
        final InitGame.Request initGameMessage = new InitGame.Request();

        final List<ServerPlayerSnap> playerSnaps = new ArrayList<>();
        final Map<Id<UserProfile>, String> names = new HashMap<>();
        final Map<Id<UserProfile>, String> colors = new HashMap<>();
        final Map<Id<UserProfile>, String> gunColors = new HashMap<>();

        colors.put(userId, Config.SELF_COLOR);
        gunColors.put(userId, Config.SELF_GUN_COLOR);
        colors.put(gameSession.getEnemy(self).getId(), Config.ENEMY_COLOR);
        gunColors.put(gameSession.getEnemy(self).getId(), Config.ENEMY_GUN_COLOR);

        final Collection<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        for (GameUser player : players) {
            playerSnaps.add(player.generateSnap());
            names.put(player.getId(), player.getUserProfile().getLogin());
        }

        initGameMessage.setSelf(userId);
        initGameMessage.setSelfSquareId(gameSession.getSelf(userId).getSquare().getId());
        initGameMessage.setColors(colors);
        initGameMessage.setGunColors(gunColors);
        initGameMessage.setNames(names);
        initGameMessage.setPlayers(playerSnaps);
        return initGameMessage;
    }
}
