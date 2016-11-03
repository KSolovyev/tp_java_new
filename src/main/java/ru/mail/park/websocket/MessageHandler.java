package ru.mail.park.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.io.IOException;

/**
 * Created by Solovyev on 06/04/16.
 */
public abstract class MessageHandler<T> {
    @NotNull
    private final Class<T> clazz;

    public MessageHandler(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    public void handleMessage(@NotNull Message message, @NotNull Id<UserProfile> forUser) throws HandleException {
        try {
            final Object data = new ObjectMapper().readValue(message.getContent(), clazz);

            //noinspection ConstantConditions
            handle(clazz.cast(data), forUser);
        } catch (IOException | ClassCastException ex) {
            throw new HandleException("Can't read incoming message of type " + message.getType() + " with content: " + message.getContent(), ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull Id<UserProfile> forUser) throws HandleException;
}
