package com.marco.goblet.controller;

import com.marco.goblet.controller.dto.ConnectRequest;
import com.marco.goblet.exception.GameNotFoundException;
import com.marco.goblet.exception.InvalidGameException;
import com.marco.goblet.exception.InvalidParamException;
import com.marco.goblet.model.Game;
import com.marco.goblet.model.Move;
import com.marco.goblet.model.Player;
import com.marco.goblet.service.GameService;
import com.marco.goblet.storage.GameStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody Player player){
        log.info("start game request: {}", player);
        return ResponseEntity.ok(gameService.createGame(player));
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId()));
    }

    @PostMapping("/connect/random")
    public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws InvalidParamException, InvalidGameException, GameNotFoundException {
        log.info("connect random: {}", player);
        return ResponseEntity.ok(gameService.connectToRandomGame(player));
    }


    @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody Move move) throws InvalidGameException, GameNotFoundException {
        log.info("gameplay: {}", move);
        Game game = gameService.gamePlay(move);
        log.info("gameplay: {}", game);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }
}
