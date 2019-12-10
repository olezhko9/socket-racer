package com.racing.client;

public class Obstacle {
    private int posX;
    private int posY;

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public Obstacle(int initX, int initY) {
        this.posX = initX;
        this.posY = initY;
    }
}
