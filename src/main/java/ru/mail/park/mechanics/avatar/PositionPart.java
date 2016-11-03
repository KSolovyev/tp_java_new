package ru.mail.park.mechanics.avatar;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Direction;

/**
 * Created by Solovyev on 03/11/2016.
 */
public class PositionPart {
    @NotNull
    private Coords body;
    @NotNull
    private Coords mouse;
    @NotNull
    private Direction direction;

    public PositionPart() {
        body = new Coords(0.0f, 0.0f);
        mouse = new Coords(0.0f, 0.0f);
        direction = Direction.None;
    }

    @NotNull
    public Coords getBody() {
        return body;
    }

    @NotNull
    public Coords getMouse() {
        return mouse;
    }

    public void setBody(@NotNull Coords body) {
        this.body = body;
    }

    public void setMouse(@NotNull Coords mouse) {
        this.mouse = mouse;
    }

    @NotNull
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
    }
}
