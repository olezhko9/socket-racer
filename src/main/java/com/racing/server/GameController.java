package com.racing.server;

import com.google.gson.Gson;
import com.racing.client.Settings;
import com.racing.models.Car;
import com.racing.models.Obstacle;

import java.util.*;


public class GameController implements Runnable{
    private int playersCount;
    private List<Car> cars = new ArrayList<Car>();
    private List<Obstacle> obstacles = new ArrayList<>();

    private int roadOffset = 5;
    private int cameraSpeed = 5;
    private Thread obstaclesFactory = new Thread(this);

    public GameController(int playersCount) {
        this.playersCount = playersCount;

        for (int i = 0; i < 4; i++) {
            cars.add(new Car(i, 65 + 130 * (i + 1), Settings.W_HEIGHT - Settings.CAR_HEIGHT - Settings.HEADER_HEIGHT - 15));
        }
    }

    public void start() {
        obstaclesFactory.start();
    }

    public String getState() {
        return "OBSTACLES:" + new Gson().toJson(obstacles) + ";" +
                "ROAD:" + Integer.toString(roadOffset) + ";" +
                "CARS:" + new Gson().toJson(cars) + ";";
    }

    public void updateState() {
        this.roadOffset = (this.roadOffset + this.cameraSpeed) % (Settings.W_HEIGHT - Settings.HEADER_HEIGHT);

        try {
            Iterator<Obstacle> obstacleIterator = obstacles.iterator();
            while (obstacleIterator.hasNext()) {
                Obstacle obstacle = obstacleIterator.next();
                if (obstacle.getPosY() > Settings.W_HEIGHT) {
                    obstacleIterator.remove();
                } else {
                    obstacle.setPosY(obstacle.getPosY() + cameraSpeed);
                }
            }
        } catch (ConcurrentModificationException e) {}
    }

    public void resetState() {
        this.roadOffset = 5;
        this.obstacles.clear();
        this.cars.clear();
        for (int i = 0; i < 4; i++) {
            cars.add(new Car(i, 65 + 130 * (i + 1), Settings.W_HEIGHT - Settings.CAR_HEIGHT - Settings.HEADER_HEIGHT - 15));
        }
    }

    public void moveCar(int carId, String direction) {
        for (Car car : cars) {
            if (car.getId() == carId) {
                if (direction.equals("LEFT")) {
                    car.moveToLeft();
                } else if (direction.equals("RIGHT")) {
                    car.moveToRight();
                }
                break;
            }
        }
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
