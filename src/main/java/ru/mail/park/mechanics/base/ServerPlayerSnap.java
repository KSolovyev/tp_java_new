package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class ServerPlayerSnap {
    @NotNull
    private Id<UserProfile> userId;
    @NotNull
    private Direction direction;
    @NotNull
    private Coords mouse;
    @NotNull
    private Coords body;
    private boolean isFiring;


    public Id<UserProfile> getUserId() {
        return userId;
    }

    public void setUserId(Id<UserProfile> userId) {
        this.userId = userId;
    }

    @NotNull
    public Direction getDirection() {
        return direction;
    }

    @NotNull
    public Coords getMouse() {
        return mouse;
    }

    @JsonProperty("isFiring")
    public boolean isFiring() {
        return isFiring;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setMouse(Coords mouse) {
        this.mouse = mouse;
    }

    public void setFiring(boolean firing) {
        isFiring = firing;
    }

    public Coords getBody() {
        return body;
    }

    public void setBody(Coords body) {
        this.body = body;
    }
}
