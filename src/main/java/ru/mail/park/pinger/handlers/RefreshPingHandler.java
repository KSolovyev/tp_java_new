package ru.mail.park.pinger.handlers;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.mail.park.pinger.PingService;
import ru.mail.park.pinger.requests.RefreshPing;
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
public class RefreshPingHandler extends MessageHandler<RefreshPing.Request> {
    @NotNull
    private PingService pingService;
    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public RefreshPingHandler(@NotNull PingService pingService, @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(RefreshPing.Request.class);
        this.pingService = pingService;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(RefreshPing.Request.class, this);
    }

    @Override
    public void handle(@NotNull RefreshPing.Request message, @NotNull Id<UserProfile> userName) throws HandleException {
        pingService.refreshPing(userName);
    }
}
