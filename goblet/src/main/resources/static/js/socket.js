const url = 'http://localhost:8080';
let stompClient;
var gameId;
var login;
var isWhitePlayer;

function connectToSocket(gameId) {
    console.log("connecting to the game");
    let socket = new SockJS(url + "/gameplay");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + gameId, function (response) {
            let data = JSON.parse(response.body);
            displayResponse(data);
        })
    })
}

function create_game() {
    login = document.getElementById("login").value;
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        $.ajax({
            url: url + "/game/start",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": login
            }),
            success: function (data) {
                gameId = data.gameId;
                connectToSocket(gameId);
                alert("Your created a game. Game id is: " + data.gameId);
                isWhitePlayer= true;
                gameOn = true;
                initializeInventory(data);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}


function connectToRandom() {
    login = document.getElementById("login").value;
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        $.ajax({
            url: url + "/game/connect/random",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "login": login
            }),
            success: function (data) {
                console.log("this should print");
                alert("Congrats you're playing with: " + data.whitePlayer.login);
                gameId = data.gameId;
                isWhitePlayer= false;
                console.log("Game ID: " + gameId);
                connectToSocket(gameId);
                gameOn = true;
                initializeInventory(data);
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}

function connectToSpecificGame() {
    login = document.getElementById("login").value;
    if (login == null || login === '') {
        alert("Please enter login");
    } else {
        gameId = document.getElementById("game_id").value;
        if (gameId == null || gameId === '') {
            alert("Please enter game id");
        }
        $.ajax({
            url: url + "/game/connect",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "player": {
                    "login": login
                },
                "gameId": gameId
            }),
            success: function (data) {
                alert("Congrats you're playing with: " + data.whitePlayer.login);
                gameId = data.gameId;
                console.log("Game ID: " + gameId);
                connectToSocket(gameId);
                gameOn = true;
                isWhitePlayer= false;
                initializeInventory(data);
                console.log("Did this work?");
            },
            error: function (error) {
                console.log(error);
            }
        })
    }
}