package ru.mail.park.pinger.handlers;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.mail.park.pinger.PingService;
import ru.mail.park.pinger.requests.PingData;
import ru.mail.park.websocket.HandleException;
import ru.mail.park.websocket.MessageHandler;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.MessageHandlerContainer;

import javax.annotation.PostConstruct;

/**
 * Created by Solovyev on 06/04/16.
 */
@Component
public class PingDataHandler extends MessageHandler<PingData.Response> {

    @NotNull
    private PingService pingService;
    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public PingDataHandler(@NotNull PingService pingService, @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(PingData.Response.class);
        this.pingService = pingService;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(PingData.Response.class, this);
    }

    @Override
    public void handle(@NotNull PingData.Response message, @NotNull Id<UserProfile> userName) throws HandleException {
        //noinspection ConstantConditions
        pingService.rememberPing(userName, message.getTimestamp(), Id.of(message.getId()));
    }
}
