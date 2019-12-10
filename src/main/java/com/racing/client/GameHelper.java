package com.racing.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameHelper extends JPanel implements ActionListener, Runnable {
    private Timer gameTick = new Timer((int) 1000 / 60, this);

    Image roadImage1 = new ImageIcon(getClass().getResource("/road.png")).getImage();
    Image roadImage2 = new ImageIcon(getClass().getResource("/road.png")).getImage();
    Image obstacleImage = new ImageIcon(getClass().getResource("/box.png")).getImage();

    private int roadOffset = 5;
    private int cameraSpeed = 5;

    private List<Car> cars = new ArrayList<Car>();
    private List<Obstacle> obstacles = new ArrayList<>();
    Thread obstaclesFactory = new Thread(this);

    private class CarController extends KeyAdapter {
        public void keyPressed(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                cars.get(0).moveToRight();
            } else if (event.getKeyCode() == KeyEvent.VK_LEFT) {
                cars.get(0).moveToLeft();
            }
        }
    }

    public GameHelper() {
        final String[] carColors = {"RED", "BLUE", "GREEN", "YELLOW"};
        for (int i = 0; i < 1; i++) {
            cars.add(new Car(carColors[i], 65 + 130 * (i + 1), Settings.W_HEIGHT - Settings.CAR_HEIGHT - Settings.HEADER_HEIGHT - 15));
        }

        setFocusable(true);
        addKeyListener(new CarController());
        gameTick.start();
        obstaclesFactory.start();
    }

    public void paint(Graphics graphics) {
        if (this.roadOffset == 0) {
            ((Graphics2D) graphics).drawImage(roadImage1, 0, roadImage2.getHeight(null) + roadOffset, null);
            ((Graphics2D) graphics).drawImage(roadImage2, 0, -roadOffset, null);
        }
        ((Graphics2D) graphics).drawImage(roadImage1, 0, roadOffset, null);
        ((Graphics2D) graphics).drawImage(roadImage2, 0, -roadImage2.getHeight(null) + roadOffset, null);

        for (Car car : cars) {
            ((Graphics2D) graphics).drawImage(car.getImage(), car.getPosX(), car.getPosY(), null);
        }

        Iterator<Obstacle> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()) {
            Obstacle obstacle = obstacleIterator.next();
            if (obstacle.getPosY() > Settings.W_HEIGHT) {
                obstacleIterator.remove();
            } else {
                ((Graphics2D) graphics).drawImage(obstacleImage, obstacle.getPosX(), obstacle.getPosY(), null);
                obstacle.setPosY(obstacle.getPosY() + cameraSpeed);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.roadOffset = (this.roadOffset + this.cameraSpeed) % (Settings.W_HEIGHT - Settings.HEADER_HEIGHT);
        repaint();
    }

    @Override
    public void run() {
        while (true) {
            Random random = new Random();
            try {
                Thread.sleep(1000);
                this.obstacles.add(
                        new Obstacle(
                        random.nextInt(Settings.W_WIDTH - Settings.ROAD_LEFT_BORDER - Settings.ROAD_RIGHT_BORDER - 60) + Settings.ROAD_LEFT_BORDER,
                                -random.nextInt(Settings.W_HEIGHT)
                        ));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
