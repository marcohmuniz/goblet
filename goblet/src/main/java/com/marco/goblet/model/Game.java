package com.marco.goblet.model;

import java.util.*;

public class Game {
    private String gameId;
    private Player whitePlayer;
    private Player blackPlayer;
    private GameStatus status;

    private static final int BOARD_SIZE = 4;
    private static final int NUM_STACKSs = 3;
    private static final int NUM_CUPS = 4;

    private ArrayList<ArrayList<Stack<Cup>>> board;

    private ArrayList<Stack<Cup>> whiteInventory;
    private ArrayList<Stack<Cup>> blackInventory;

    private boolean isWhiteMove;
    private Player winner;

    public Game(){
        isWhiteMove = true;
        board = new ArrayList<ArrayList<Stack<Cup>>>();
        initializeBoard();
        whiteInventory = new ArrayList<Stack<Cup>>();
        blackInventory = new ArrayList<Stack<Cup>>();
        initializePlayerInventory(true);
        initializePlayerInventory(false);
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public ArrayList<ArrayList<Stack<Cup>>> getBoard(){
        return board;
    }

    public ArrayList<Stack<Cup>> getWhiteInventory(){
        return whiteInventory;
    }

    public ArrayList<Stack<Cup>> getBlackInventory(){
        return blackInventory;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public void setGameId(String gameId){
        this.gameId = gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getGameId(){
        return gameId;
    }

    public void initializeBoard(){
        for(int i = 0; i < BOARD_SIZE; i++){
            board.add(new ArrayList<Stack<Cup>>());
            for(int j = 0; j < BOARD_SIZE; j++){
                board.get(i).add(new Stack<Cup>());
            }
        }
    }

    public void initializePlayerInventory(boolean color){
        ArrayList<Stack<Cup>> inventory = color ? whiteInventory : blackInventory;
        for(int i = 0; i < NUM_STACKSs ; i++){
            inventory.add(new Stack<Cup>());
            for(int j = 0; j < NUM_CUPS; j++){
                inventory.get(i).push(new Cup(j, color));
            }
        }
    }

    private boolean withinBounds(int[] move){
        if((move[0] < 0 || move[0] > BOARD_SIZE - 1) || (move[1] < 0 || move[1] > BOARD_SIZE - 1) ){
            return false;
        }
        return true;
    }

    //inventory is three stacks filled with 4 cups in descending order 3->0
    private Cup getFromInventory(int whichStack){
        ArrayList<Stack<Cup>> inventory = isWhiteMove ? whiteInventory : blackInventory;
        //if no more cups return null, otherwise pop cup from chosen stack
        if(inventory.get(whichStack).empty()){
            return null;
        } else {
            return inventory.get(whichStack).peek();
        }
    }


    //see later if you can turn print statement into throws going into a try catch in Goblet
    public boolean makeMove(int[] start, int[] end, int whichStack){
        if(whichStack > NUM_STACKSs - 1 || whichStack < -1){
            System.out.println("Choose from stack");
            return false;
        }
        boolean isFromInventory = (whichStack != -1);

        //always check the end location regardless of whether the move
        //comes from inventory
        if(!withinBounds(end)){
            System.out.println("Choose a legal move.");
        }
        //if the move is not from inventory check the bounds of start, that you move the cup somewhere
        //that there is a piece there that is yours
        if(!isFromInventory){
            if(!withinBounds(start)){
                System.out.println("Choose a legal move.");
                return false;
            }
            if(start[0] == end[0] && start[1] == end[1]){
                System.out.println("Not valid move.");
                return false;
            }
            if(board.get(start[0]).get(start[1]).empty()){
                System.out.println("There is no piece there");
                return false;
            }
            if(board.get(start[0]).get(start[1]).peek().getCupColor() != isWhiteMove){
                System.out.println("Not your piece!");
                return false;
            }
        }
        Cup startCup;
        //get the cup, either from inventory or from the board
        if(isFromInventory){
            startCup = getFromInventory(whichStack);
            //if there is no cup in your inventory return
            if(startCup == null){
                System.out.println("Sorry, you don't have anymore of those cups");
                return false;
            }
        } else {
            startCup = board.get(start[0]).get(start[1]).peek();
        }
        //if the place you're placing your cup is not empty and/or has a bigger cup than you return
        if(!(board.get(end[0]).get(end[1]).empty()) && !(startCup.isBiggerThan(board.get(end[0]).get(end[1]).peek()))){
            System.out.println("That cup is bigger than yours!");
            return false;
        }
        //established the end location either is empty or has a smaller cup, so make your move
        board.get(end[0]).get(end[1]).push(startCup);

        ArrayList<Stack<Cup>> inventory = isWhiteMove ? whiteInventory : blackInventory;
        //pop the player stack or the board
        if(isFromInventory){
            inventory.get(whichStack).pop();
        } else{
            board.get(start[0]).get(start[1]).pop();
        }
        isWhiteMove = !isWhiteMove;
        return true;
    }

    //find any four in a row, a player can lose on their turn. No mercy!
    public boolean isGameOver(){
        for(ArrayList<Stack<Cup>> row : board){
            int colorTracker = 0;
            for(Stack<Cup> cell : row){
                if(cell.empty()){
                    break;
                } else {
                    if(cell.peek().getCupColor() == true){
                        colorTracker++;
                    } else {
                        colorTracker--;
                    }
                }
            }
            if(colorTracker == BOARD_SIZE){
                winner = whitePlayer;
                return true;
            } else if(colorTracker == BOARD_SIZE * -1){
                winner = blackPlayer;
                return true;
            }
        }

        for(int i = 0; i<BOARD_SIZE; i++){
            int colorTracker = 0;
            for(ArrayList<Stack<Cup>> row : board){
                if(row.get(i).empty()){
                    break;
                } else {
                    if(row.get(i).peek().getCupColor() == true){
                        colorTracker++;
                    } else {
                        colorTracker--;
                    }
                }
            }
            if(colorTracker == BOARD_SIZE){
                winner = whitePlayer;
                return true;
            } else if(colorTracker == BOARD_SIZE * -1){
                winner = blackPlayer;
                return true;
            }
        }

        int colIndex = 0;
        int colorTracker = 0;
        for(ArrayList<Stack<Cup>> row : board){
            if(row.get(colIndex).empty()){
                colIndex++;
                break;
            } else {
                if(row.get(colIndex).peek().getCupColor()){
                    colorTracker++;
                } else {
                    colorTracker--;
                }
                colIndex++;
            }
            if(colorTracker == BOARD_SIZE){
                winner = whitePlayer;
                return true;
            } else if(colorTracker == BOARD_SIZE * -1){
                winner = blackPlayer;
                return true;
            }
        }

        colIndex = 3;
        colorTracker = 0;
        for(ArrayList<Stack<Cup>> row : board){
            if(row.get(colIndex).empty()){
                colIndex--;
                break;
            } else {
                if(row.get(colIndex).peek().getCupColor()){
                    colorTracker++;
                } else {
                    colorTracker--;
                }
                colIndex--;
            }
            if(colorTracker == BOARD_SIZE){
                winner = whitePlayer;
                return true;
            } else if(colorTracker == BOARD_SIZE * -1){
                winner = blackPlayer;
                return true;
            }
        }
        return false;
    }

    public Player getWinner(){
        return winner;
    }

    public String toString(){
        StringBuilder boardString = new StringBuilder();
        for(ArrayList<Stack<Cup>> row : board){
            for(Stack<Cup> cell : row){
                if(cell.empty()){
                    boardString.append(" |   ");
                } else {
                    boardString.append(" |");
                    boardString.append(String.format("%3s", cell.peek().getCupColorString() + cell.peek().getSize()));
                }
            }
            boardString.append("  |");
            boardString.append("\n");
        }
        return boardString.toString();
    }

    public String inventoryToString(boolean color){
        ArrayList<Stack<Cup>> inventory = color ? whiteInventory : blackInventory;
        StringBuilder inventoryString = new StringBuilder();
        String invInfo = color ? "White Inventory" : "Black Inventory";
        inventoryString.append(invInfo +"\n");
        for(Stack<Cup> playerStack : inventory){
            inventoryString.append(" | " + (playerStack.size()-1));
        }
        inventoryString.append(" |" + "\n");
        return inventoryString.toString();
    }

    public boolean isWhiteTurn(){
        return isWhiteMove;
    }
}