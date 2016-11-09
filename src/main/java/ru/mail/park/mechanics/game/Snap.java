package ru.mail.park.mechanics.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Solovyev on 06/11/2016.
 */
public interface Snap<T> {

    @NotNull
    @JsonProperty("name")
    String getPartName();
}
