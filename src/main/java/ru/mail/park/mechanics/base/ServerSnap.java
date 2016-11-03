package ru.mail.park.mechanics.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Solovyev on 03/11/2016.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class ServerSnap {

    @NotNull List<ServerPlayerSnap> players;
    private long serverFrameTime;

    @NotNull
    public List<ServerPlayerSnap> getPlayers() {
        return players;
    }

    public void setPlayers(@NotNull List<ServerPlayerSnap> players) {
        this.players = players;
    }

    public long getServerFrameTime() {
        return serverFrameTime;
    }

    public void setServerFrameTime(long serverFrameTime) {
        this.serverFrameTime = serverFrameTime;
    }
}
