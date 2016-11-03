package ru.mail.park.mechanics;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.mechanics.avatar.GameUser;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;


/**
 * @author k.solovyev
 */
public class GameSession {
    @NotNull
    private final GameUser first;
    @NotNull
    private final GameUser second;

    public GameSession(@NotNull UserProfile user1, @NotNull UserProfile user2) {
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

}
