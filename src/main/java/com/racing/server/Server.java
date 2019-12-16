package com.racing.server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


class ClientConnection extends Thread {

    private int clientId;
    private Socket socket;
    private GameController gameController;
    private BufferedReader in;
    private BufferedWriter out;

    public ClientConnection(int clientId, Socket socket, GameController gameController) throws IOException {
        this.socket = socket;
        this.clientId = clientId;
        this.gameController = gameController;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run() {
        String word;
        try {
            while (true) {
                word = in.readLine();
                if(word.equals("stop")) {
                    this.downService();
                    break;
                }
                gameController.moveCar(this.clientId, word);
            }
        } 
        catch (NullPointerException ignored) {} 
        catch (IOException e) {
            this.downService();
        }
    }

    public void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ClientConnection connection : Server.serverList) {
                    if(connection.equals(this)) connection.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}


public class Server {

    static final int PLAYERS_COUNT = 2;
    public static final int PORT = 9000;
    public static LinkedList<ClientConnection> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server Started");

        GameController gameController = new GameController(PLAYERS_COUNT);

        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ClientConnection(serverList.size(), socket, gameController));
                    if (serverList.size() == PLAYERS_COUNT) {
                        startGame(gameController);
                    }
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }

    private static void startGame(GameController gameController) {
        gameController.start();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                updateGamePlay();
            }

            private void updateGamePlay() {
                if (serverList.size() > 0) {
                    gameController.updateState();
                } else {
                    timer.cancel();
                    gameController.resetState();
                }
                for (ClientConnection connection : serverList){
                    connection.send(gameController.getState());
                }
            }
        },0, (int) 1000 / 60);
    }
}