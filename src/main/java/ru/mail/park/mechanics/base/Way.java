package ru.mail.park.mechanics.base;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings({"EnumeratedConstantNamingConvention", "EnumeratedClassNamingConvention"})
public enum Way {
    Left(new Direction(Math.PI)),
    Right(new Direction(0)),
    Up(new Direction(Math.PI /2)),
    Down(new Direction(3 * Math.PI / 2)),
    None(Direction.NO_WHERE);

    @NotNull
    private Direction radial;

    Way(@NotNull Direction radial) {
        this.radial = radial;
    }

    public Direction getRadial() {
        return radial;
    }
}
