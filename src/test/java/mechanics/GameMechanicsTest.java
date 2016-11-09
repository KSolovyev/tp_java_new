package mechanics;

import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import ru.mail.park.mechanics.Config;
import ru.mail.park.mechanics.GameMechanics;
import ru.mail.park.mechanics.GameSession;
import ru.mail.park.mechanics.MechanicsExecutor;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.mechanics.avatar.PositionPart;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Way;
import ru.mail.park.mechanics.internal.GameSessionService;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.services.AccountService;
import ru.mail.park.websocket.RemotePointService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Solovyev on 06/11/2016.
 */
@SuppressWarnings({"MagicNumber", "NullableProblems", "SpringJavaAutowiredMembersInspection"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class GameMechanicsTest {

    @SuppressWarnings("unused")
    @MockBean
    private RemotePointService remotePointService;
    @SuppressWarnings("unused")
    @MockBean
    private MechanicsExecutor mechanicsExecutor;
    @SuppressWarnings("unused")
    @MockBean
    private WebSocketServerFactory defaultHandshakeHandler;
    @Autowired
    private GameMechanics gameMechanics;
    @Autowired
    private AccountService accountService;
    @Autowired
    private GameSessionService gameSessionService;
    @NotNull
    private UserProfile user1;
    @NotNull
    private UserProfile user2;

    @Before
    public void setUp () {
        when(remotePointService.isConnected(any())).thenReturn(true);
        user1 = accountService.addUser("user1", "", "");
        user2 = accountService.addUser("user2", "", "");

    }

    @Test
    public void movementTest() {
        final GameSession gameSession = startGame(user1.getId(), user2.getId());
        @NotNull final GameUser firstPlayer = gameSession.getSelf(gameSession.getFirst().getId());
        @SuppressWarnings({"TooBroadScope", "LongLine"})
        final double startY = firstPlayer.getSquare().claimPart(PositionPart.class).getBody().y;
        gameMechanics.addClientSnapshot(user1.getId(), createClientSnap(Way.Down, 50));
        gameMechanics.addClientSnapshot(user1.getId(), createClientSnap(Way.Down, 50));
        gameMechanics.gmStep(100);
        Assert.assertEquals(startY + Config.SQUARE_SPEED * 50 * 2,
                firstPlayer.getSquare().claimPart(PositionPart.class).getBody().y, 0.01d);
    }

    @Test
    public void gameStartedTest () {
        startGame(user1.getId(), user2.getId());
    }

    private ClientSnap createClientSnap(@NotNull Way movementDirection, long frameTime) {
        final ClientSnap clientSnap = new ClientSnap();
        clientSnap.setDirection(movementDirection);
        clientSnap.setFrameTime(frameTime);
        clientSnap.setFiring(false);
        clientSnap.setMouse(new Coords(0,0));
        return clientSnap;
    }

    @NotNull
    private GameSession startGame(@NotNull Id<UserProfile> player1, @NotNull Id<UserProfile> player2) {
        gameMechanics.addUser(player1);
        gameMechanics.addUser(player2);
        gameMechanics.gmStep(0);
        @Nullable final GameSession gameSession = gameSessionService.getSessionForUser(player1);
        Assert.assertNotNull("Game session should be started on closest tick, but it didn't", gameSession);
        return gameSession;
    }


}

