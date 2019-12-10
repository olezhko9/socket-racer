package com.racing.client;

import javax.swing.*;
import java.awt.*;

public class Road extends JPanel {
    Image image = new ImageIcon(getClass().getResource("/road.png")).getImage();

    public void paint(Graphics graphics) {
        ((Graphics2D) graphics).drawImage(image, 0, 0, null);
    }
}
