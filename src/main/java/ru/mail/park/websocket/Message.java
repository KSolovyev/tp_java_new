package ru.mail.park.websocket;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Solovyev on 06/04/16.
 */
@SuppressWarnings("NullableProblems")
public class
Message {
    @NotNull
    private String type;
    @NotNull
    private String content;
    //Here could be your versioning system
    //private int version = VERSION;

    @NotNull
    public String getType() {
        return type;
    }
    @NotNull
    public String getContent() {
        return content;
    }

    public Message() {
    }

    public Message(@NotNull String type, @NotNull String content) {
        this.type = type;
        this.content = content;
    }

    public Message(@NotNull Class clazz, @NotNull String content) {
        //noinspection ConstantConditions
        this(clazz.getName(), content);
    }
}
