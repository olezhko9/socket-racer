package com.racing.models;

import com.racing.client.Settings;

import java.awt.*;

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

    public Rectangle getCollider() {
        return new Rectangle(this.posX, this.posY, Settings.BOX_WIDTH, Settings.BOX_WIDTH);
    }
}
