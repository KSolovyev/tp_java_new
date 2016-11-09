package ru.mail.park.mechanics.avatar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.mail.park.mechanics.base.Coords;
import ru.mail.park.mechanics.base.Direction;
import ru.mail.park.mechanics.base.Way;
import ru.mail.park.mechanics.game.GamePart;
import ru.mail.park.mechanics.game.Snap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings("unused")
public class PositionPart implements GamePart {
    @NotNull
    private Coords body;

    @NotNull
    private Direction movingTo;

    @NotNull
    private final List<Coords> desirablePath = new ArrayList<>();

    public PositionPart() {
        body = new Coords(0.0f, 0.0f);

        movingTo = Way.None.getRadial();
    }

    public void executeMovement() {
        if(!desirablePath.isEmpty()) {
            body = desirablePath.get(desirablePath.size() - 1);
        }
        desirablePath.clear();
    }

    @NotNull
    public List<Coords> getDesirablePath() {
        return desirablePath;
    }

    @NotNull
    public Coords getLastDesirablePoint() {
        if (desirablePath.isEmpty()) {
            return body;
        }
        return desirablePath.get(desirablePath.size() - 1);
    }

    public void addDesirableCoords(@Nullable Coords desirableCoords) {
        desirablePath.add(desirableCoords);
    }

    @NotNull
    public Coords getBody() {
        return body;
    }

    public void setBody(@NotNull Coords body) {
        this.body = body;
    }

    @NotNull
    public Direction getMovingTo() {
        return movingTo;
    }

    public void setMovingTo(@NotNull Direction movingTo) {
        this.movingTo = movingTo;
    }

    @Override
    public boolean shouldBeSnaped() {
        return true;
    }

    @Override
    public PositionSnap takeSnap() {
        return new PositionSnap(this);
    }

    public static final class PositionSnap implements Snap<PositionPart>{
        @NotNull
        private final Coords body;
        @NotNull
        private final Direction movingTo;

        public PositionSnap(@NotNull PositionPart positionPart) {
            body = positionPart.body;
            movingTo = positionPart.movingTo;
        }

        @NotNull
        @Override
        public String getPartName() {
            return PositionPart.class.getSimpleName();
        }

        @NotNull
        public Coords getBody() {
            return body;
        }
        @NotNull
        public Direction getMovingTo() {
            return movingTo;
        }
    }
}
