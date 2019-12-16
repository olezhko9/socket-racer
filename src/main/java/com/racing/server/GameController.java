package com.racing.server;

import com.google.gson.Gson;
import com.racing.client.Settings;
import com.racing.models.Car;
import com.racing.models.Obstacle;

import java.util.ArrayList;
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
//        return new Gson().toJson(obstacles);
        return Integer.toString(roadOffset);
    }

    public void updateState() {
        roadOffset = (roadOffset + cameraSpeed) % (Settings.W_HEIGHT - Settings.HEADER_HEIGHT);
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
