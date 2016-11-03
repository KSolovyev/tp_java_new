package ru.mail.park.mechanics.internal;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.mail.park.mechanics.Config;
import ru.mail.park.mechanics.GameSession;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.mechanics.avatar.PositionPart;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Direction;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.util.*;

/**
 * Not thread safe! Per game mechanic service
 * Created by Solovyev on 03/11/2016.
 */
@Service
public class ClientSnapshotsService {

    private Map<Id<UserProfile>, List<ClientSnap>> snaps = new HashMap<>();

    public void pushClientSnap(@NotNull Id<UserProfile> user, @NotNull ClientSnap snap) {
        this.snaps.putIfAbsent(user, new ArrayList<>());
        final List<ClientSnap> clientSnaps = snaps.get(user);
        clientSnaps.add(snap);
    }

    @NotNull
    public List<ClientSnap> getSnapForUser(@NotNull Id<UserProfile> user) {
        return snaps.getOrDefault(user, Collections.emptyList());
    }

    public void processSnapshotsFor(@NotNull GameSession gameSession) {
        final List<GameUser> players = new ArrayList<>();
        players.add(gameSession.getFirst());
        players.add(gameSession.getSecond());
        for (GameUser player : players) {
            final List<ClientSnap> playerSnaps = getSnapForUser(player.getId());
            if (playerSnaps.isEmpty()) {
                continue;
            }
            for (ClientSnap snap : playerSnaps) {
                processMovement(player, snap.getDirection(), snap.getFrameTime());
            }
            final ClientSnap lastSnap = playerSnaps.get(playerSnaps.size() - 1);
            processMouseMove(player, lastSnap.getMouse());
            processDirection(player, lastSnap.getDirection());

            //TODO:Firing
        }
    }

    private void processMovement(@NotNull GameUser gameUser, @NotNull Direction direction, long frameTime) {
        final PositionPart positionPart = gameUser.getPositionPart();
        final Coords body = positionPart.getBody();
        switch (direction) {
            case Left: {
                final float newX = Math.max(0, body.x - Config.SQUARE_SPEED * frameTime);
                positionPart.setBody(new Coords(newX, body.y));
                break;
            }
            case Right: {
                final float newX = Math.min(Config.PLAYGROUND_WIDTH - Config.SQUARE_SIZE, body.x + Config.SQUARE_SPEED * frameTime);
                positionPart.setBody(new Coords(newX, body.y));
                break;
            }
            case Up: {
                final float newy = Math.max(0, body.y - Config.SQUARE_SPEED * frameTime);
                positionPart.setBody(new Coords(body.x, newy));
                break;
            }
            case Down: {
                final float newY = Math.min(Config.PLAYGROUND_HEIGHT - Config.SQUARE_SIZE, body.y + Config.SQUARE_SPEED * frameTime);
                positionPart.setBody(new Coords(body.x, newY));
                break;
            }
            case None: {
                break;
            }
        }
    }

    private void processMouseMove(@NotNull GameUser gameUser, @NotNull Coords mouse) {
        gameUser.getPositionPart().setMouse(mouse);
    }

    private void processDirection(@NotNull GameUser gameUser, @NotNull Direction direction) {
        gameUser.getPositionPart().setDirection(direction);
    }


    public void clear() {
        snaps.clear();
    }

}
