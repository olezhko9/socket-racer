package com.racing.server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

class ClientConnection extends Thread {

    private Socket socket; // сокет, через который сервер общается с клиентом
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток завписи в сокет

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
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
                System.out.println("Echoing: " + word);
//                for (ClientConnection connection : Server.serverList) {
//                    connection.send(word);
//                }
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

    public static final int PORT = 9000;
    public static LinkedList<ClientConnection> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server Started");

        GameController gameController = new GameController(4);
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
                }
                for (ClientConnection connection : serverList){
                    connection.send(gameController.getState());
                }
            }

        },0, (int) 1000 / 30);


        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ClientConnection(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}