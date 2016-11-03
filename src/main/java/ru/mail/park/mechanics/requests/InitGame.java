package ru.mail.park.mechanics.requests;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.mechanics.base.ServerPlayerSnap;
import ru.mail.park.model.Id;
import ru.mail.park.model.UserProfile;

import java.util.List;
import java.util.Map;

/**
 * Created by Solovyev on 03/11/2016.
 */
public class InitGame {
    @SuppressWarnings("NullableProblems")
    public static final class Request {
        @NotNull
        private Id<UserProfile> self;
        @NotNull
        private List<ServerPlayerSnap> players;
        @NotNull
        private Map<Id<UserProfile>, String> colors;
        @NotNull
        private Map<Id<UserProfile>, String> gunColors;
        @NotNull
        private Map<Id<UserProfile>, String> names;

        public Map<Id<UserProfile>, String> getNames() {
            return names;
        }

        public void setNames(Map<Id<UserProfile>, String> names) {
            this.names = names;
        }

        @NotNull
        public Id<UserProfile> getSelf() {
            return self;
        }

        public void setSelf(@NotNull Id<UserProfile> self) {
            this.self = self;
        }
        @NotNull
        public List<ServerPlayerSnap> getPlayers() {
            return players;
        }
        @NotNull
        public void setPlayers(@NotNull List<ServerPlayerSnap> players) {
            this.players = players;
        }
        @NotNull
        public Map<Id<UserProfile>, String> getColors() {
            return colors;
        }

        public void setColors(@NotNull Map<Id<UserProfile>, String> colors) {
            this.colors = colors;
        }
        @NotNull
        public Map<Id<UserProfile>, String> getGunColors() {
            return gunColors;
        }

        public void setGunColors(@NotNull Map<Id<UserProfile>, String> gunColors) {
            this.gunColors = gunColors;
        }
    }

}
