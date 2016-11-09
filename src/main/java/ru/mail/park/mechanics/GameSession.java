package ru.mail.park.mechanics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.util.concurrent.atomic.AtomicLong;


/**
 * @author k.solovyev
 */
public class GameSession {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    @NotNull
    private final Id<GameSession> sessionId;
    @NotNull
    private final GameUser first;
    @NotNull
    private final GameUser second;

    public GameSession(@NotNull UserProfile user1, @NotNull UserProfile user2) {
        this.sessionId = Id.of(ID_GENERATOR.getAndIncrement());
        this.first = new GameUser(user1);
        this.second =  new GameUser(user2);
    }

    @NotNull
    public GameUser getEnemy(@NotNull GameUser user) {
        if (user == first) {
            return second;
        }
        if (user == second) {
            return first;
        }
        throw new IllegalArgumentException("Requested enemy for game but user not participant");
    }

    @NotNull
    public GameUser getSelf(@NotNull Id<UserProfile> userId) {
        if (first.getUserProfile().getId().equals(userId)) {
            return first;
        }
        if (second.getUserProfile().getId().equals(userId)) {
            return second;
        }
        throw new IllegalArgumentException("Request self for game but user not participate it");
    }

    @NotNull
    public GameUser getFirst() {
        return first;
    }

    @NotNull
    public GameUser getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameSession that = (GameSession) o;

        return sessionId.equals(that.sessionId);

    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }
}
