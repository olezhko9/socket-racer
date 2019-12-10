package main.java.com.racing.client;

import javax.swing.*;

public class Main {
    static final int W_WIDTH = 900;
    static final int W_HEIGHT = 600;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Socket racer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(W_WIDTH, W_HEIGHT);
        frame.setVisible(true);
    }
}
