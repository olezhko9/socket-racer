package com.racing.server;

import com.google.gson.Gson;
import com.racing.client.Settings;
import com.racing.models.Car;
import com.racing.models.Obstacle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameController implements Runnable{
    private int playersCount;
    private List<Car> cars = new ArrayList<Car>();
    private List<Obstacle> obstacles = new ArrayList<>();

    private int roadOffset = 5;
    private int cameraSpeed = 5;
    private Thread obstaclesFactory = new Thread(this);

    public GameController(int playersCount) {
        this.playersCount = playersCount;

    }

    public void start() {
        obstaclesFactory.start();
    }

    public String getState() {
        return "OBSTACLES:" + new Gson().toJson(obstacles) + ";" +
                "ROAD:" + Integer.toString(roadOffset) + ";";
    }

    public void updateState() {
        this.roadOffset = (this.roadOffset + this.cameraSpeed) % (Settings.W_HEIGHT - Settings.HEADER_HEIGHT);

        Iterator<Obstacle> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()) {
            Obstacle obstacle = obstacleIterator.next();
            if (obstacle.getPosY() > Settings.W_HEIGHT) {
                obstacleIterator.remove();
            } else {
                obstacle.setPosY(obstacle.getPosY() + cameraSpeed);
            }
        }
    }

    public void resetState() {
        this.roadOffset = 5;
        this.obstacles.clear();
    }

    @Override
    public void run() {
        // obstacles generator
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
