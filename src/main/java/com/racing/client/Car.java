package com.racing.client;

import javax.swing.*;
import java.awt.*;

public class Car {
    private Image image;
    private int posX;
    private int posY;
    static final int speedX = 10;

    public Car(String color, int initX, int initY) {
        this.posX = initX;
        this.posY = initY;
        this.image = new ImageIcon(getClass().getResource("/" + color + ".png")).getImage();
    }

    public Image getImage() { return image; }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void moveToLeft() {
        if (this.posX - speedX < Settings.ROAD_LEFT_BORDER)
            return;
        this.posX -= speedX;
    }

    public void moveToRight() {
        if (this.posX + speedX > Settings.W_WIDTH - Settings.ROAD_RIGHT_BORDER - Settings.CAR_WIDTH)
            return;
        this.posX += speedX;
    }

    public Rectangle getCollider() {
        return new Rectangle(this.posX, this.posY, Settings.CAR_WIDTH, Settings.CAR_HEIGHT);
    }
}
