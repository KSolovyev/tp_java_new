package ru.mail.park.mechanics.avatar;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.mechanics.base.ServerPlayerSnap;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

/**
 * Created by Solovyev on 01/11/2016.
 */
public class GameUser {
    @NotNull
    private final UserProfile userProfile;
    @NotNull
    private final TimingPart timingPart;
    private final Square square;

    //TODO: Collider


    public GameUser(@NotNull UserProfile userProfile) {
        this.userProfile = userProfile;
        square = new Square();
        this.timingPart = new TimingPart();
    }

    @NotNull
    public TimingPart getTimingPart() {
        return timingPart;
    }

    @NotNull
    public UserProfile getUserProfile() {
        return userProfile;
    }

    public Square getSquare() {
        return square;
    }

    @NotNull
    public Id<UserProfile> getId() {
        return userProfile.getId();
    }

    @NotNull
    public ServerPlayerSnap generateSnap() {

        final ServerPlayerSnap result = new ServerPlayerSnap();
        result.setUserId(getId());
        result.setPlayerSquare(square.getSnap());
        return result;
    }
}
