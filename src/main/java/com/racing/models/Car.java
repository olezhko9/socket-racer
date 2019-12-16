package com.racing.models;

import com.racing.client.Settings;

import java.awt.Rectangle;


public class Car {
    private int carId;
    private int posX;
    private int posY;
    static final int speedX = 10;

    public Car(int carId, int initX, int initY) {
        this.carId = carId;
        this.posX = initX;
        this.posY = initY;
    }

    public int getId() { return carId; }

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
