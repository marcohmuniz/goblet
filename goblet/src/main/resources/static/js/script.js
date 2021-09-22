/*
TODO
1. Highlight last played move, maybe with green.
2. Move on and come back in like a month to refactor
*/


var gameOn = false;
var currStart = null;
var currEnd = null;
var currWhichStack = null;
var emptyColor = "#e8e2cda8";
var selectedColor = "#b18d50";

//add some user side move checking here
function playerTurn() {
    if (gameOn){
        let start = currStart.attr('id').split('_').map(Number);
        if(currWhichStack !== null){
            let whichStack = currWhichStack.attr('id').split('_').map(Number)[1];
            //Game API expects the final location to be end, but in this file the start is the first location that is chosen
            makeAMove([-1,-1], start , whichStack);

        } else {
            let end = currEnd.attr('id').split('_').map(Number);;
            makeAMove(start, end, -1);
        }
        clearMoveSelectors();
    }
}

function makeAMove(start, end, whichStack) {
    login = document.getElementById("login").value;
    $.ajax({
        url: url + "/game/gameplay",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            "gameId": gameId,
             "player":{
                "login": login
             },
            "start": start,
            "end": end,
            "whichStack": whichStack
        }),
        success: function (data) {
            displayResponse(data);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function displayResponse(data) {
    if(isWhitePlayer){
        displayMessage(data.whiteErrorMessage);
    } else {
        displayMessage(data.blackErrorMessage);
    }

    if(data.status === "FINISHED"){
        gameOverMessage(data);
    }

    let board = data.board;

    for(let i = 0; i < board.length; i++){
        for(let j = 0; j < board[i].length; j++){
            let id = i + "_" + j;
            let currPosition = board[i][j];
            updatePosition(id, currPosition);
        }
    }

    let inventory = {};
    let opponentInventory = {};
    if(login === data.whitePlayer.login){
        inventory = data.whiteInventory;
        opponentInventory = data.blackInventory;
    } else {
        inventory = data.blackInventory;
        opponentInventory = data.whiteInventory;
    }

    for(let i = 0; i < inventory.length; i++){
        let id = "inv_" + i;
        let opponentId = "oinv_" + i

        let currPosition = inventory[i];
        let opponentCurrPosition = opponentInventory[i];

        updatePosition(id, currPosition);
        updatePosition(opponentId, opponentCurrPosition);
    }
}

function displayMessage(message){
    $("#message").text(message);
}

function updatePosition(id, position){
    let cup = $("#" + id).children("div").eq(0);
    cup.removeClass();

    if(position.length !== 0){
        let currCup = position[position.length - 1];
        let cupColorClass = currCup.cupColor ? "cupColorWhite" : "cupColorBlack";
        let cupSizeClass = "cupSize" + currCup.size.toString();

        cup.addClass(cupColorClass);
        cup.addClass(cupSizeClass);
    }
}

function gameOverMessage(data){
    let message = "";
    if(login === data.winner.login){
        message = "You've won!"
    } else {
        message = "You've lost!"
    }
    $("#message").text(message);
}

function initializeInventory(data){
    let startingCupClass = "cupSize3"
    let opponentCupColorClass = "";
    let cupColorClass = "";
    if(isWhitePlayer){
        cupColorClass = "cupColorWhite";
        opponentCupColorClass = "cupColorBlack";
    } else {
        cupColorClass = "cupColorBlack";
        opponentCupColorClass = "cupColorWhite";
    }

    for(let i = 0; i < 3; i++){
        let id = "inv_" + i;
        let opponentId = "oinv_" + i;
        let cup = $("#" + id).children("div").eq(0);
        let opponentCup = $("#" + opponentId).children("div").eq(0);
        cup.removeClass();
        cup.addClass(cupColorClass);
        cup.addClass(startingCupClass);
        opponentCup.removeClass();
        opponentCup.addClass(opponentCupColorClass);
        opponentCup.addClass(startingCupClass);
        }
}
//first click is start, second is end
//change naming so makes sense with inventory
$(".cup").click(function () {
    if(currStart == null){
        $(this).addClass("selected");
        $(this).css("background-color", selectedColor);
        currStart = $(this);
        if(currWhichStack !== null){
            playerTurn();
        }                                 
    } else if(currEnd == null){
        if($(this).attr('id') === currStart.attr('id')){
           currStart.removeClass("selected");
           currStart.removeAttr("style");
           currStart = null;
        } else {
            $(this).addClass("selected");
            $(this).css("background-color", selectedColor);
            currEnd = $(this);
            if(currWhichStack === null){
                playerTurn();
            }
        }
    } else {
        currStart.removeClass("selected");
        currStart.removeAttr("style");
        currStart = null;
        currEnd.removeClass("selected");
        currEnd.removeAttr("style");
        currEnd = null;
    }
});

//radio button for selecting inventory
//get rid of toggleClass to make simpler to read
$(".inv").click(function () {
    $(this).toggleClass("selected");
    if(currWhichStack === null){
        $(this).css("background-color", selectedColor);
        currWhichStack = $(this);
    } else {
        if($(this).attr('id') === currWhichStack.attr('id')){
            if($(this).hasClass("selected")){
                $(this).css("background-color", thisColor);
                currWhichStack = $(this);
            } else {
                $(this).removeAttr("style");
                currWhichStack = null;
            }
        } else {
            $(this).css("background-color", selectedColor);
            currWhichStack.removeAttr("style");
            currWhichStack.toggleClass("selected");
            currWhichStack = $(this);
        }
    }
});

function clearMoveSelectors(){
    if(currStart !== null){
        currStart.removeClass("selected");
        currStart.removeAttr("style");
        currStart = null;
    }

    if(currEnd !== null){
        currEnd.removeClass("selected");
        currEnd.removeAttr("style");
        currEnd = null;
    }

    if(currWhichStack !== null){
        currWhichStack.removeClass("selected");
        currWhichStack.removeAttr("style");
        currWhichStack = null;
    }
}
