package com.marco.goblet.model;

import lombok.Data;

@Data
public class Move {
    private String gameId;
    private Player player;
    private int[] start;
    private int[] end;
    private int whichStack;
}
