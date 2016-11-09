package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings("PublicField")
public class Coords {

    public Coords(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        this.x = x;
        this.y = y;
    }

    public final double x;
    public final double y;

    @NotNull
    public Coords add(@NotNull Coords addition) {
        return new Coords(x + addition.x, y + addition.y);
    }
}
