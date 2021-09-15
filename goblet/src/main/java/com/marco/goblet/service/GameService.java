package com.marco.goblet.service;

import com.marco.goblet.exception.GameNotFoundException;
import com.marco.goblet.exception.InvalidGameException;
import com.marco.goblet.exception.InvalidParamException;
import com.marco.goblet.model.Game;
import com.marco.goblet.model.GameStatus;
import com.marco.goblet.model.Move;
import com.marco.goblet.model.Player;
import com.marco.goblet.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    public Game createGame(Player player){
        Game game = new Game();
        game.setGameId(UUID.randomUUID().toString());
        game.setWhitePlayer(player);
        game.setStatus(GameStatus.NEW);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToGame(Player blackPlayer, String gameId) throws InvalidParamException, InvalidGameException {
        if(!GameStorage.getInstance().getGames().containsKey(gameId)){
            throw new InvalidParamException("Game with provided id does not exist.");
        }
        Game game = GameStorage.getInstance().getGames().get(gameId);
        if(game.getBlackPlayer() != null){
            throw new InvalidGameException("Game is not valid anymore, it already has a second player.");
        }

        game.setBlackPlayer(blackPlayer);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToRandomGame(Player blackPlayer) throws GameNotFoundException {
        Game game = GameStorage.getInstance().getGames().values().stream().
                filter(it -> it.getStatus().equals(GameStatus.NEW))
                .findFirst().orElseThrow(() -> new GameNotFoundException("There are no new games to join. Create one!"));

        game.setBlackPlayer(blackPlayer);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game gamePlay(Move move) throws GameNotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(move.getGameId())) {
            throw new GameNotFoundException("Game with provided id does not exist.");
        }

        Game game = GameStorage.getInstance().getGames().get(move.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            throw new InvalidGameException("Game already finished.");
        }

        //ensure correct player is making move
        Player playerWithTurn = game.isWhiteTurn() ? game.getWhitePlayer() : game.getBlackPlayer();
        if(!move.getPlayer().equals(playerWithTurn)){
            System.out.println("It's not your turn.");
            return game;
        }
        game.makeMove(move.getStart(), move.getEnd(), move.getWhichStack());
        if(game.isGameOver()){
            game.setStatus(GameStatus.FINISHED);
        }

        GameStorage.getInstance().setGame(game);
        return game;
    }

}
