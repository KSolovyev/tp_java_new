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
    private UserProfile userProfile;
    @NotNull
    private TimingPart timingPart;
    @NotNull
    private PositionPart positionPart;

    //TODO: Collider


    public GameUser(@NotNull UserProfile userProfile) {
        this.userProfile = userProfile;
        this.timingPart = new TimingPart();
        this.positionPart = new PositionPart();
    }

    @NotNull
    public TimingPart getTimingPart() {
        return timingPart;
    }

    @NotNull
    public UserProfile getUserProfile() {
        return userProfile;
    }

    @NotNull
    public PositionPart getPositionPart() {
        return positionPart;
    }

    @NotNull
    public Id<UserProfile> getId() {
        return userProfile.getId();
    }

    @NotNull
    public ServerPlayerSnap generateSnap() {
        final ServerPlayerSnap result = new ServerPlayerSnap();
        result.setUserId(getId());
        result.setBody(positionPart.getBody());
        result.setMouse(positionPart.getMouse());
        result.setDirection(positionPart.getDirection());
        //TODO: Firing
        result.setFiring(false);
        return result;
    }
}
