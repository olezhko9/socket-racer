package com.racing.client;

import java.net.*;
import java.io.*;


public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private GameHelper gameHelper;

    public Client(GameHelper gameHelper, String host, int port) {
        this.gameHelper = gameHelper;
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new ReadMsg().start();
        } catch (IOException e) {
            Client.this.downService();
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {
        }
    }

    // нить чтения сообщений с сервера
    private class ReadMsg extends Thread {
        @Override
        public void run() {

            String event;
            try {
                while (true) {
                    event = in.readLine();
                    if (event.equals("stop")) {
                        Client.this.downService();
                        break;
                    }
//                    System.out.println(event);
                    gameHelper.updateState(event);
                }
            } catch (IOException e) {
                Client.this.downService();
            }
        }
    }

    public void send(String message) {
        try {
            if (message.equals("stop")) {
                out.write("stop" + "\n");
                this.downService();
            } else {
                out.write(message + "\n");
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            this.downService();
        }
    }
}
