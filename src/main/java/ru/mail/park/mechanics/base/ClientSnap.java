package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Direction;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class ClientSnap {


    @NotNull
    private Direction direction;
    @NotNull
    private Coords mouse;
    private boolean isFiring;
    private long frameTime;

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

    public long getFrameTime() {
        return frameTime;
    }

    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
    }

    public void setMouse(@NotNull Coords mouse) {
        this.mouse = mouse;
    }

    public void setFiring(boolean firing) {
        isFiring = firing;
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }
}
