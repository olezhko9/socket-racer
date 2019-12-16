package com.racing.client;

import com.google.gson.Gson;
import com.racing.models.Car;
import com.racing.models.Obstacle;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;


public class GameHelper implements ActionListener {
    private Timer gameTick = new Timer((int) 1000 / 60, this);
    private Boolean isGameOver = false;

    Image roadImage1 = new ImageIcon(getClass().getResource("/road.png")).getImage();
    Image roadImage2 = new ImageIcon(getClass().getResource("/road.png")).getImage();
    Image obstacleImage = new ImageIcon(getClass().getResource("/box.png")).getImage();

    private List<Car> cars = new ArrayList<Car>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private int roadOffset;
    private Client client;
    private JPanel panel;
    final String[] carColors = {"RED", "BLUE", "GREEN", "YELLOW"};

    public JPanel getPanel() {
        return panel;
    }

    private class CarController extends KeyAdapter {
        public void keyPressed(KeyEvent event) {
            if (isGameOver) return;
            if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                client.send("RIGHT");
            } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
                client.send("LEFT");
            }
        }
    }

    public GameHelper() {
        String host = "localhost";
        int port = 9000;
        this.client = new Client(this, host, port);
        this.panel = new JPanel() {
            @Override
            public void paint(Graphics graphics) {
                super.paint(graphics);
                GameHelper.this.paint(graphics);
            }
        };

        this.panel.setFocusable(true);
        this.panel.addKeyListener(new CarController());
        gameTick.start();
    }

    private void paint(Graphics graphics) {
        if (this.roadOffset == 0) {
            graphics.drawImage(roadImage1, 0, roadImage2.getHeight(null) + roadOffset, null);
            graphics.drawImage(roadImage2, 0, -roadOffset, null);
        }
        graphics.drawImage(roadImage1, 0, roadOffset, null);
        graphics.drawImage(roadImage2, 0, -roadImage2.getHeight(null) + roadOffset, null);

        for (Car car : cars) {
            graphics.drawImage(new ImageIcon(getClass().getResource("/" + carColors[car.getId()] + ".png")).getImage(), car.getPosX(), car.getPosY(), null);
        }

        for (Obstacle obstacle : obstacles) {
            graphics.drawImage(obstacleImage, obstacle.getPosX(), obstacle.getPosY(), null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.repaint();
    }

    public void updateState(String gameState) {
        String[] states = gameState.split(";");
        for (String state : states) {
            String[] data = state.split(":", 2);
            if (data[0].equals("ROAD")) {
                this.roadOffset = Integer.parseInt(data[1]);
            } else if (data[0].equals("OBSTACLES")) {
                this.obstacles = Arrays.asList(new Gson().fromJson(data[1], Obstacle[].class));
            } else if (data[0].equals("CARS")) {
                this.cars = Arrays.asList(new Gson().fromJson(data[1], Car[].class));
            }
        }
    }
}
