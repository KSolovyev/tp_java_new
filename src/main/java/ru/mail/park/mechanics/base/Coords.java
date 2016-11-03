package ru.mail.park.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings("PublicField")
public class Coords {

    public Coords(@JsonProperty("x") float x, @JsonProperty("y") float y) {
        this.x = x;
        this.y = y;
    }

    public final float x;
    public final float y;
}
