package mechanics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.mechanics.avatar.PositionPart;
import ru.mail.park.mechanics.avatar.Square;
import ru.mail.park.mechanics.base.ClientSnap;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Way;
import ru.mail.park.mechanics.base.ServerPlayerSnap;
import ru.mail.park.mechanics.game.GamePart;
import ru.mail.park.mechanics.game.Snap;
import ru.mail.park.mechanics.requests.InitGame;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.io.IOException;
import java.util.*;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings("MagicNumber")
public class SnapSerializationTest {

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
        objectMapper.writeValueAsString(clientSnap);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    public void serverSnapTest() throws IOException {
        final ServerPlayerSnap serverPlayerSnap = new ServerPlayerSnap();
        serverPlayerSnap.setUserId(Id.of(4));
        final ObjectMapper objectMapper = new ObjectMapper();
        final String result = objectMapper.writeValueAsString(serverPlayerSnap);
        objectMapper.readValue(result, ServerPlayerSnap.class);
    }

    @SuppressWarnings({"TooBroadScope", "OverlyBroadThrowsClause"})
    @Test
    public void serverInitTest() throws IOException {
        final InitGame.Request initGame = new InitGame.Request();
        final UserProfile pupkin = new UserProfile("Pupkin", "");
        final GameUser gameUser = new GameUser(pupkin);
        final ServerPlayerSnap serverPlayerSnap = gameUser.generateSnap();
        final Map<Id<UserProfile>, String> names = new HashMap<>();
        final Map<Id<UserProfile>, String> colors = new HashMap<>();
        final Map<Id<UserProfile>, String> gunColors = new HashMap<>();
        final List<ServerPlayerSnap> players = new ArrayList<>();
        players.add(serverPlayerSnap);
        names.put(gameUser.getId(), "Pupkin");
        colors.put(gameUser.getId(), "345");
        gunColors.put(gameUser.getId(), "42342");
        initGame.setPlayers(players);
        initGame.setColors(colors);
        initGame.setGunColors(gunColors);
        initGame.setSelf(gameUser.getId());
        initGame.setSelfSquareId(gameUser.getSquare().getId());
        initGame.setNames(names);
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValueAsString(initGame);
    }

    @Test
    public void snapTest() throws JsonProcessingException {
        final Square square = initSquare();
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonObject squareJson = new JsonParser().parse(objectMapper.writeValueAsString(square.getSnap()))
                .getAsJsonObject();
        Assert.assertNotNull(squareJson.getAsJsonPrimitive("id"));
        final JsonArray parts;
        Assert.assertNotNull(parts = squareJson.getAsJsonArray("partSnaps"));

        final Iterator<JsonElement> iterator = parts.iterator();
        JsonObject positionPart = null;
        JsonObject mousePart = null;
        while(iterator.hasNext()) {
            final JsonObject partJson = iterator.next().getAsJsonObject();

            if (partJson.getAsJsonPrimitive("name").getAsString().equals("PositionPart")) {
                positionPart = partJson;
            }
            if (partJson.getAsJsonPrimitive("name").getAsString().equals("MousePart")) {
                mousePart = partJson;
            }
        }
        Assert.assertNotNull(positionPart);
        Assert.assertNotNull(mousePart);
        Assert.assertNotNull(positionPart.getAsJsonObject("body"));
        Assert.assertNotNull(positionPart.getAsJsonObject("movingTo"));
        Assert.assertNotNull(mousePart.getAsJsonObject("mouse"));
    }

    private Square initSquare() {
        final Square square = new Square();
        final PositionPart part = square.getPart(PositionPart.class);
        Assert.assertNotNull(part);
        part.setMovingTo(Way.Up.getRadial());
        part.setBody(new Coords(100.0f, 100.0f));
        return square;
    }



    @Test
    public void uniquePartNamesTest() {
        final Square square = new Square();
        final Collection<String> partNames = new HashSet<>();

        for (Snap<? extends GamePart> snap : square.getSnap().getPartSnaps()) {
            Assert.assertFalse(partNames.contains(snap.getPartName()));
            partNames.add(snap.getPartName());
        }

    }
}
