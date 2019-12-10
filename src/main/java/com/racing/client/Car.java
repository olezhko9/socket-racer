package com.racing.client;

import javax.swing.*;
import java.awt.*;

public class Car {
    private Image image;
    private int posX;
    private int posY;

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
}
