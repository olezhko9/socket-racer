package com.racing.client;

import javax.swing.*;

public class Main {
    static final int W_WIDTH = 840;
    static final int W_HEIGHT = 650;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Socket racer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(W_WIDTH, W_HEIGHT);

        frame.add(new Road());
        frame.setVisible(true);
    }
}
