package ru.mail.park.pinger.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.mail.park.pinger.PingService;
import ru.mail.park.websocket.RemotePointService;
import ru.mail.park.pinger.TimingData;
import ru.mail.park.pinger.requests.GetPing;
import ru.mail.park.websocket.HandleException;
import ru.mail.park.websocket.Message;
import ru.mail.park.websocket.MessageHandler;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;
import ru.mail.park.websocket.MessageHandlerContainer;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by Solovyev on 06/04/16.
 */
@Component
public class GetPingHandler extends MessageHandler<GetPing.Request> {

    @NotNull
    private RemotePointService remotePointService;
    @NotNull
    private PingService pingService;
    @NotNull
    private MessageHandlerContainer messageHandlerContainer;

    public GetPingHandler(@NotNull PingService pingService,
                          @NotNull RemotePointService remotePointService,
                          @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(GetPing.Request.class);
        this.remotePointService = remotePointService;
        this.pingService = pingService;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(GetPing.Request.class, this);
    }

    @Override
    public void handle(@NotNull GetPing.Request message, @NotNull Id<UserProfile> userId) throws HandleException {
        //noinspection ConstantConditions
        final TimingData timings = pingService.getTimings(userId);

        final GetPing.Response.Builder builder = GetPing.Response.create();
        if (timings != null) {
            builder
                    .ping(timings.getClientPing())
                    .clientTimeShift(timings.getClientTimeshift());

        }
        try {
            final Message response = new Message(GetPing.Response.class, new ObjectMapper().writeValueAsString(builder.build()));
            remotePointService.sendMessageToUser(userId, response);
        } catch (JsonProcessingException e) {
            throw new HandleException("Unnable to contruct json for ping request "+ userId, e);
        } catch (IOException e) {
            throw new HandleException("Unnable to send answer back to user "+ userId, e);
        }
    }
}
