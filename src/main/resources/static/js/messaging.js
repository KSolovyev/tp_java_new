var MessagingTools = function (Game) {
    var getPingMessage = {};
    getPingMessage.type="ru.mail.park.pinger.requests.GetPing$Request";
    getPingMessage.content="{}";

    var joinGameMessage = {}
    joinGameMessage.type = "ru.mail.park.mechanics.requests.JoinGame$Request";
    joinGameMessage.content="{}";

    this.sendUpdatePingMsg = function () {
        Game.socket.send(JSON.stringify(getPingMessage));
    };

    this.sendJoinGameMsg = function () {
        Game.socket.send(JSON.stringify(joinGameMessage));
    };

    this.sendClientSnap = function(snap) {
        var clientSnapMessage = {};
        clientSnapMessage.type = "ru.mail.park.mechanics.base.ClientSnap";
        clientSnapMessage.content = JSON.stringify(snap);
        Game.socket.send(JSON.stringify(clientSnapMessage));
    }
};
