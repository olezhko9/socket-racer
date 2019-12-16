package com.racing.client;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


class ServerConnection {
    private Socket socket;
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток чтения в сокет
    private BufferedReader consoleInput; // поток чтения с консоли

    public ServerConnection(String host, int port) {
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            consoleInput = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new ReadMsg().start();
            new WriteMsg().start();
        } catch (IOException e) {
            ServerConnection.this.downService();
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
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
                        ServerConnection.this.downService();
                        break;
                    }
                    System.out.println(event);
                }
            } catch (IOException e) {
                ServerConnection.this.downService();
            }
        }
    }

    // нить отправляющая сообщения приходящие с консоли на сервер
    public class WriteMsg extends Thread {

        @Override
        public void run() {
            while (true) {
                String message;
                try {
                    String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    message = consoleInput.readLine();
                    if (message.equals("stop")) {
                        out.write("stop" + "\n");
                        ServerConnection.this.downService(); 
                        break;
                    } else {
                        out.write("(" + date + ") " + ": " + message + "\n"); // отправляем на сервер
                    }
                    out.flush();
                } catch (IOException e) {
                    ServerConnection.this.downService();
                }
            }
        }
    }
}

public class Client {

    private static String host = "localhost";
    private static int port = 9000;

    public static void main(String[] args) {
        new ServerConnection(host, port);
    }
}
