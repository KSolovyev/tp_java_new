import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Direction;
import ru.mail.park.mechanics.base.ServerPlayerSnap;
import ru.mail.park.mechanics.requests.InitGame;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings("MagicNumber")
public class ClientSnapTest {

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    public void clientSnapTest() throws IOException {
        final String clientSnapStr =
                "{ " +
                        "\"direction\":\"Left\"," +
                        "\"mouse\":{" +
                            "\"x\":34.4," +
                            "\"y\":55.4" +
                        "}," +
                        "\"isFiring\":\"false\"," +
                        "\"frameTime\":\"32\"" +
                        '}';
        final ObjectMapper objectMapper = new ObjectMapper();
        final ClientSnap clientSnap = objectMapper.readValue(clientSnapStr, ClientSnap.class);
        final String result = objectMapper.writeValueAsString(clientSnap);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    public void serverSnapTest() throws IOException {
        final ServerPlayerSnap serverPlayerSnap = new ServerPlayerSnap();
        serverPlayerSnap.setBody(new Coords(1.2f, 1.3f));
        serverPlayerSnap.setMouse(new Coords(1.2f, 1.3f));
        serverPlayerSnap.setDirection(Direction.None);
        serverPlayerSnap.setUserId(Id.of(4));
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writeValueAsString(serverPlayerSnap);
        final ServerPlayerSnap recognizedPlayerSnap = objectMapper.readValue(result, ServerPlayerSnap.class);
    }

    @SuppressWarnings({"TooBroadScope", "OverlyBroadThrowsClause"})
    @Test
    public void serverInitTest() throws IOException {
        final InitGame.Request initGame = new InitGame.Request();
        final ServerPlayerSnap serverPlayerSnap = new ServerPlayerSnap();
        serverPlayerSnap.setBody(new Coords(1.2f, 1.3f));
        serverPlayerSnap.setMouse(new Coords(1.2f, 1.3f));
        serverPlayerSnap.setDirection(Direction.None);
        serverPlayerSnap.setUserId(Id.of(4));
        final HashMap<Id<UserProfile>, String> names = new HashMap<>();
        final HashMap<Id<UserProfile>, String> colors = new HashMap<>();
        final HashMap<Id<UserProfile>, String> gunColors = new HashMap<>();
        final List<ServerPlayerSnap> players = new ArrayList<>();
        players.add(serverPlayerSnap);
        names.put(Id.of(4), "Pupkin");
        colors.put(Id.of(4), "345");
        gunColors.put(Id.of(4), "42342");
        initGame.setPlayers(players);
        initGame.setColors(colors);
        initGame.setGunColors(gunColors);
        initGame.setSelf(Id.of(4));
        initGame.setNames(names);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writeValueAsString(initGame);
    }
}
