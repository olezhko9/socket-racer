package com.racing.client;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Socket racer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Settings.W_WIDTH, Settings.W_HEIGHT);

        GameHelper gameHelper = new GameHelper();
        frame.add(gameHelper.getPanel());
        frame.setVisible(true);
    }
}
