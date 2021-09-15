package com.marco.goblet.controller.dto;

import com.marco.goblet.model.Player;
import lombok.Data;

@Data
public class ConnectRequest {
    private Player player;
    private String gameId;
}
