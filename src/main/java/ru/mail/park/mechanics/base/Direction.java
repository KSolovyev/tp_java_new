package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Solovyev on 04/11/2016.
 */
public class Direction {
    private final double angle;

    public Direction(@JsonProperty double angle) {
        this.angle = angle;
    }

    public Coords getVector(double length) {
        return new Coords(length * Math.cos(angle), length * Math.sin(angle));
    }

    public double getAngle() {
        return angle;
    }

    public static final Direction NO_WHERE = new Direction(0) {
        @Override
        public Coords getVector(double length) {
            return new Coords(0.0f, 0.0f);
        }
    };
}
