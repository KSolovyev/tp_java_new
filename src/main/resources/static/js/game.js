var Game = {};
var Messaging = new MessagingTools(Game);

Game.fps = 30;
Game.socket = null;
Game.nextFrame = null;
Game.interval = null;
Game.direction = 'None';
Game.squareSize = 30;
Game.gunLength = 30;
Game.gunWidth = 5;
Game.mouse = {x: 0.0, y: 0.0};
Game.clientReinterpt = 50;
Game.serverSnaps = [];
Game.userId = null;
Game.userSquareId = null;
Game.frameTimeLeft = 0;

function ClientSnap() {
    this.direction = "None";
    this.mouse = {x: 0.0, y: 0.0};
    this.isFiring = false;
    this.frameTime = 0;
}

function PlayerSnap(snapMsg) {
    this.id = 0;
    this.square = {
        id:null,
        body : {x: 0.0, y: 0.0},
        direction : "None",
        lookingAt:{x: 0.0, y: 0.0},
        isFiring : false
    };

    if (snapMsg != null) {
        var squareSnap = snapMsg.playerSquare;
        this.id = snapMsg.userId;
        this.square.id = squareSnap.id;
        for(var i = 0; i < squareSnap.partSnaps.length; i++) {
            var part = squareSnap.partSnaps[i];
            if (part.name === "PositionPart") {
                this.square.body = part.body;
                //TODO: direction
            }
            else if(part.name === "MousePart") {
                this.square.lookingAt = part.mouse;
            }
            //TODO: firing
        }
    }
}
function ServerSnap() {
    this.players = [];
    this.serverFrameTime = 0;
    this.elapsed  = 0;
}

function Square() {
    this.name = "";
    this.color = null;
    this.gunColor = null;
    this.body = {x: 0.0, y: 0.0};
    this.mouse = {x: 0.0, y: 0.0};
}

Square.prototype.draw = function (context) {
    context.fillStyle = this.color;
    context.fillRect(this.body.x, this.body.y, Game.squareSize, Game.squareSize);
    var center = {x: this.body.x + Game.squareSize / 2, y: this.body.y + Game.squareSize / 2};
    var dmouseX = this.mouse.x - center.x;
    var dmouseY = this.mouse.y - center.y;

    var gun = {
        dx: Game.gunLength * dmouseX / Math.sqrt(dmouseX * dmouseX + dmouseY * dmouseY),
        dy: Game.gunLength * dmouseY / Math.sqrt(dmouseX * dmouseX + dmouseY * dmouseY)
    };
    context.save();
    context.beginPath();
    context.moveTo(center.x, center.y);
    context.strokeStyle = this.gunColor;
    context.lineTo(center.x + gun.dx, center.y + gun.dy);
    context.lineWidth = Game.gunWidth;
    context.stroke();
    context.restore();
};

Game.initialize = function () {
    this.entities = [];
    canvas = document.getElementById('playground');
    if (!canvas.getContext) {
        Console.log('Error: 2d canvas not supported by this browser.');
        return;
    }
    var buttons = [];
    var getDirectionByCode = function (code) {
        if (code === 65 || code === 37) {
            return 'Left';
        } else if (code === 87 || code === 38) {
            return 'Up';
        } else if (code === 68 || code === 39) {
            return 'Right';
        } else if (code === 83 || code === 40) {
            return 'Down';
        }
        else return 'None'
    };

    this.context = canvas.getContext('2d');
    window.addEventListener('keydown', function (e) {
        var code = e.keyCode;
        if (code > 36 && code < 41 || code == 65 || code == 87 || code == 68 || code == 83) {
            Game.direction = getDirectionByCode(code);
            buttons[Game.direction] = true;
        }

    }, false);
    window.addEventListener('keyup', function (e) {
        var code = e.keyCode;
        if (code > 36 && code < 41 || code == 65 || code == 87 || code == 68 || code == 83) {
            var disabledDirection = getDirectionByCode(code);
            buttons[disabledDirection] = false;
            //trying to find another pushed button
            var directions = Object.keys(buttons);
            for (var i = 0; i < directions.length; i++) {
                if (buttons[directions[i]] === true) {
                    Game.direction = directions[i];
                    return
                }
            }
            Game.direction = "None"
        }
    }, false);
    window.addEventListener('mousemove', function (e) {
        if (Game.userId != null) {
            Game.entities[Game.userSquareId].mouse.x = e.clientX;
            Game.entities[Game.userSquareId].mouse.y = e.clientY;
        }
    });

    Game.connect();
};

Game.startGameLoop = function () {
    if (window.requestAnimationFrame) {
        Game.nextFrame = function () {
            requestAnimationFrame(Game.run);
        };
    } else if (window.webkitRequestAnimationFrame) {
        Game.nextFrame = function () {
            webkitRequestAnimationFrame(Game.run);
        };
    } else if (window.mozRequestAnimationFrame) {
        Game.nextFrame = function () {
            mozRequestAnimationFrame(Game.run);
        };
    } else {
        Game.interval = setInterval(Game.run, 1000 / Game.fps);
    }
    if (Game.nextFrame != null) {
        Game.nextFrame();
    }
};

Game.stopGameLoop = function () {
    Game.nextFrame = null;
    if (Game.interval != null) {
        clearInterval(Game.interval);
    }
};

Game.draw = function () {
    this.context.clearRect(0, 0, 640, 480);
    for (var id in this.entities) {
        this.entities[id].draw(this.context);
    }
};

Game.addSquare = function (id, name,color, gunColor) {
    Game.entities[id] = new Square();
    Game.entities[id].color = color;
    Game.entities[id].gunColor = gunColor;
    Game.entities[id].name = name;
};

Game.updateSquare = function (id, body, gun) {
    if (typeof Game.entities[id] != "undefined") {
        Game.entities[id].body = body;
        Game.entities[id].mouse = gun
    }
};

Game.removeSquare = function (id) {
    Game.entities[id] = null;
    // Force GC.
    delete Game.entities[id];
};

function updatePing() {
    Game.socket.send(JSON.stringify(getPingMessage));
}
var pingPeriod = 5000;
var lastPingTime = 0;
Game.lastFrameTime = 0;
Game.initialSkipTime = Game.clientReinterpt;


Game.run = (function () {
    var skipTicks = 1000 / Game.fps, nextGameTick = (new Date).getTime();
    return function () {
        var time = (new Date).getTime();
        var frameTime = time - Game.lastFrameTime;
        while (time > nextGameTick) {
            nextGameTick += skipTicks;
        }
        if (time - lastPingTime > pingPeriod) {
            Messaging.sendUpdatePingMsg();
            lastPingTime = time;
        }
        Game.sendClientSnap(frameTime);
        if (Game.initialSkipTime < 0) {
            Game.updateObjects(frameTime);
        } else {
            Game.initialSkipTime -= frameTime;
        }
        Game.draw();
        if (Game.nextFrame != null) {
            Game.nextFrame();
        }
        Game.lastFrameTime = time;

    };
})();

Game.updateObjects = function(frameTime) {
    var moveTimeOnSnap = 0;
    var snapFinished = false;
    var player = null;
    var mouse = null;
    var moveTime = frameTime + Game.frameTimeLeft;
    //lagging
    if (Game.serverSnaps.length > 2) {
        window.console.info("lags detected")
        var snapAdjustTo =  Game.serverSnaps[Game.serverSnaps.length - 2];
        for (i = 0; i < snapAdjustTo.players.length; i++) {
            player = snapAdjustTo.players[i];
            Game.entities[player.square.id].body = player.square.body;
            if (player.id === Game.userId) {
                continue;
            }
            Game.entities[player.square.id].mouse = player.square.lookingAt;
        }
        Game.serverSnaps = [Game.serverSnaps[Game.serverSnaps.length - 1]];
        return;
    }
    while (moveTime > 0) {
        if (Game.serverSnaps.length < 1) {
            Game.frameTimeLeft += moveTime;
            return
        }
        var serverSnap = Game.serverSnaps[0];
        if (serverSnap.serverFrameTime - serverSnap.elapsed > moveTime) {
            moveTimeOnSnap = moveTime;
            snapFinished = false;
        } else {
            moveTimeOnSnap = serverSnap.serverFrameTime - serverSnap.elapsed;
            snapFinished = true;
        }
        for (i = 0; i < serverSnap.players.length; i++) {
            player = serverSnap.players[i];
            var body = Game.entities[player.square.id].body;
            var overallDX = player.square.body.x - body.x;
            var overallDY = player.square.body.y - body.y;
            body.x += overallDX * moveTimeOnSnap / serverSnap.serverFrameTime;
            body.y += overallDY * moveTimeOnSnap / serverSnap.serverFrameTime;
            // Game.entities[player.square.id].body = player.square.body;
            if (player.id === Game.userId) {
                continue;
            }
            mouse = Game.entities[player.square.id].mouse;
            overallDX = player.square.lookingAt.x - mouse.x;
            overallDY = player.square.lookingAt.y - mouse.y;
            mouse.x += overallDX * moveTimeOnSnap / serverSnap.serverFrameTime;
            mouse.y += overallDY * moveTimeOnSnap / serverSnap.serverFrameTime;
        }
        if (snapFinished) {
            Game.serverSnaps.splice(0,1);
            moveTime -= moveTimeOnSnap;
        } else {
            serverSnap.elapsed += moveTimeOnSnap;
            moveTime = 0;
        }
    }
};

Game.sendClientSnap = function (frameTime) {
    var snap = new ClientSnap();
    var me = Game.entities[Game.userSquareId];
    snap.direction = Game.direction;
    snap.mouse = me.mouse;
    snap.frameTime = frameTime;
    snap.isFiring = me.isFiring;
    Messaging.sendClientSnap(snap);
};

Game.onServerSnapArrived = function (snapRaw) {
    var serverSnap = new ServerSnap();
    serverSnap.timeArrived = (new Date()).getTime();
    for(var i = 0; i < snapRaw.players.length; i++) {
        var playerRaw = snapRaw.players[i];
        var playerSnap = new PlayerSnap(playerRaw);
        serverSnap.players.push(playerSnap);
    }
    serverSnap.serverFrameTime = snapRaw.serverFrameTime;
    Game.serverSnaps.push(serverSnap);
    //Todo: Wait for another snap and launch animation with Game.clientReinterpt delay
};

Game.tryStartGame = function () {
    Messaging.sendJoinGameMsg();
};

Game.onGameStarted = function (initMessage) {
    Game.lastFrameTime = (new Date).getTime();
    Game.userId = initMessage.self;
    Game.userSquareId = initMessage.selfSquareId;
    // for(player in initMessage.players) {
    for(var i = 0; i < initMessage.players.length; i++) {
        var playerRaw = initMessage.players[i];
        var palyerSnap = new PlayerSnap(playerRaw);
        Game.addSquare(palyerSnap.square.id, initMessage.names[palyerSnap.id],
            initMessage.colors[palyerSnap.id], initMessage.gunColors[palyerSnap.id]);

        Game.updateSquare(palyerSnap.square.id, palyerSnap.square.body, palyerSnap.square.lookingAt);

        Console.log('Info: ' + Game.entities[palyerSnap.square.id].name + ' joins the game!')
    }
    Game.startGameLoop();
};

Game.connect = (function () {
    Game.socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/game");

    Game.socket.onopen = function () {
        // Socket open.. start the game loop.
        Console.log('Info: WebSocket connection opened.');
        Console.log('Info: Waiting for another player...');
        try {
            Game.tryStartGame();
        } catch (ex) {
            Game.socket.close(1001, "error: exeception occured during the initialization stage: " + ex);
        }
    };

    Game.socket.onclose = function () {
        Console.log('Info: WebSocket closed.');
        Game.stopGameLoop();
    };

    Game.socket.onmessage = function (event) {
        var content = {};
        var responseContent = {};
        var response = {};
        var message = JSON.parse(event.data);


        if (message.type === "ru.mail.park.pinger.requests.PingData$Request") {
            content = JSON.parse(message.content);
            responseContent.id = content.id;
            responseContent.timestamp = new Date().getTime();
            response.type = "ru.mail.park.pinger.requests.PingData$Response";
            response.content = JSON.stringify(responseContent);
            Game.socket.send(JSON.stringify(response));
            return;
        }
        if (message.type === "ru.mail.park.pinger.requests.GetPing$Response") {
            content = JSON.parse(message.content);
            document.getElementById("ping").innerHTML = content.ping;
            document.getElementById("time-shift").innerHTML = content.clientTimeShift;
            return;
        }
        if (message.type === "ru.mail.park.mechanics.requests.InitGame$Request") {
            content = JSON.parse(message.content);
            Game.onGameStarted(content);
            return;
        }
        if (message.type === "ru.mail.park.mechanics.base.ServerSnap") {
            content = JSON.parse(message.content);
            Game.onServerSnapArrived(content);
            return;
        }
    };
});

var Console = {};

Console.log = (function (message) {
    var console = document.getElementById('console');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.innerHTML = message;
    console.appendChild(p);
    while (console.childNodes.length > 25) {
        console.removeChild(console.firstChild);
    }
    console.scrollTop = console.scrollHeight;
});

Game.initialize();