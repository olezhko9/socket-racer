package com.racing.server;

import com.google.gson.Gson;
import com.racing.client.Settings;
import com.racing.models.Car;
import com.racing.models.Obstacle;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class GameController implements Runnable{

    private boolean isGameStarted = false;
    private int playersCount;
    private List<Car> cars = new CopyOnWriteArrayList<>();
    private List<Obstacle> obstacles = new CopyOnWriteArrayList<>();

    private int roadOffset = 5;
    private int cameraSpeed = 4;
    private Thread obstaclesFactory = new Thread(this);

    public GameController(int playersCount) {
        this.playersCount = playersCount;
        for (int i = 0; i < playersCount; i++) {
            cars.add(new Car(i, 65 + 130 * (i + 1), Settings.W_HEIGHT - Settings.CAR_HEIGHT - Settings.HEADER_HEIGHT - 15));
        }
    }

    public void start() {
        if (!obstaclesFactory.isAlive()) {
            obstaclesFactory.start();
        }
        this.isGameStarted = true;
        System.out.println("GO GO GO");
    }

    public String getState() {
        return "OBSTACLES:" + new Gson().toJson(obstacles) + ";" +
                "ROAD:" + Integer.toString(roadOffset) + ";" +
                "CARS:" + new Gson().toJson(cars) + ";";
    }

    public void updateState() {
        this.roadOffset = (this.roadOffset + this.cameraSpeed) % (Settings.W_HEIGHT - Settings.HEADER_HEIGHT);

        for (Obstacle obstacle : obstacles) {
            if (obstacle.getPosY() > Settings.W_HEIGHT) {
                obstacles.remove(obstacle);
            } else {
                obstacle.setPosY(obstacle.getPosY() + cameraSpeed);
            }
        }
        this.testCollisions();
    }

    public void resetState() {
        this.isGameStarted = false;
        this.roadOffset = 5;
        this.obstacles.clear();
        this.cars.clear();
        for (int i = 0; i < playersCount; i++) {
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

    private void testCollisions() {
        for (Obstacle obstacle : obstacles) {
            for (Car car : cars) {
                if (car.getCollider().intersects(obstacle.getCollider())) {
                    cars.remove(car);
                    obstacles.remove(obstacle);
                }
            }
        }
    }

    @Override
    public void run() {
        // obstacles generator
        while (true) {
            if (!this.isGameStarted) continue;
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
