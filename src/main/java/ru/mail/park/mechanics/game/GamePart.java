package ru.mail.park.mechanics.game;

/**
 * Created by Solovyev on 04/11/2016.
 */
public interface GamePart {

    boolean shouldBeSnaped();

    Snap<? extends GamePart> takeSnap();
}
