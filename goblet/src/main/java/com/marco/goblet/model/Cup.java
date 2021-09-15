package com.marco.goblet.model;

public class Cup {
    private final int size;
    private final boolean isWhite;

    Cup(int size, boolean isWhite){
        this.size = size;
        this.isWhite = isWhite;
    }

    public int getSize(){
        return this.size;
    }

    public boolean isBiggerThan(Cup c){
        return this.size > c.getSize();
    }

    public boolean getCupColor(){
        return isWhite;
    }

    public String getCupColorString(){
        return isWhite ? "W" : "B";
    }
}
