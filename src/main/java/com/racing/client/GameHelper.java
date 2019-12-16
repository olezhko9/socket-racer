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
    private Timer gameTick = new Timer((int) 1000 / 30, this);

    private Boolean isGameOver = false;

    Image roadImage1 = new ImageIcon(getClass().getResource("/road.png")).getImage();
    Image roadImage2 = new ImageIcon(getClass().getResource("/road.png")).getImage();
    Image obstacleImage = new ImageIcon(getClass().getResource("/box.png")).getImage();

    private int roadOffset;

    private List<Car> cars = new ArrayList<Car>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private Client client;
    private JPanel panel;

    public JPanel getPanel() {
        return panel;
    }

    private class CarController extends KeyAdapter {
        public void keyPressed(KeyEvent event) {
            if (isGameOver) return;
            if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                cars.get(0).moveToRight();
                client.send("RIGHT");
            } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
                cars.get(0).moveToLeft();
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
        final String[] carColors = {"RED", "BLUE", "GREEN", "YELLOW"};
        for (int i = 0; i < 4; i++) {
            cars.add(new Car(carColors[i], 65 + 130 * (i + 1), Settings.W_HEIGHT - Settings.CAR_HEIGHT - Settings.HEADER_HEIGHT - 15));
        }

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
            graphics.drawImage(car.getImage(), car.getPosX(), car.getPosY(), null);
        }

        for (Obstacle obstacle : obstacles) {
            graphics.drawImage(obstacleImage, obstacle.getPosX(), obstacle.getPosY(), null);
        }
    }

    private void testCollisions() {
        Iterator<Obstacle> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()) {
            Obstacle obstacle = obstacleIterator.next();

            ListIterator<Car> carIterator = cars.listIterator();
            while (carIterator.hasNext()) {
                Car car = carIterator.next();
                if (car.getCollider().intersects(obstacle.getCollider())) {
                    if (carIterator.nextIndex() - 1 == 0) {
                        this.isGameOver = true;
                    }
                    carIterator.remove();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.repaint();
        if (!this.isGameOver) {
            testCollisions();
        }
    }

    public void updateState(String gameState) {
        String[] states = gameState.split(";");
        for (String state : states) {
            String[] data = state.split(":", 2);
            if (data[0].equals("ROAD")) {
                this.roadOffset = Integer.parseInt(data[1]);
            } else if (data[0].equals("OBSTACLES")) {
                this.obstacles = Arrays.asList(new Gson().fromJson(data[1], Obstacle[].class));
            }
        }
    }
}
