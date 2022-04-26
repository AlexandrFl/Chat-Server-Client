package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientHandler implements Runnable, Loggerable {

    private final Date DATE = new Date();
    private final String LOGGER_FILE_PATH = "D:\\Java Project\\Chat\\ServerLog.txt";
    private final File serverLog = new File(LOGGER_FILE_PATH);
    private final BufferedWriter inLogFile = new BufferedWriter(new FileWriter(serverLog, true));
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String name;
    private static int clientCount = 0;

    public ClientHandler(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.name = in.readLine();
            clientHandlers.add(this);
            clientCount++;
            sendToServerScreen("Пользователь " + name + " подключен");
            logInFile("ПОЛЬЗОВАТЕЛЬ " + name + " ПОДКЛЮЧЕН. ВСЕГО ПОЛЬЗОВАТЕЛЕЙ " + clientCount + " * " + DATE + "\n");
            sendToAll("Пользователь " + name + " подключился к чату. Всего пользователей " + clientCount);
        } catch (IOException e) {
            closeAll(socket, in, out);
        }
    }

    @Override
    public void run() {
        String msg;
        while (socket.isConnected()) {
            try {
                msg = in.readLine();
                logInFile("Получено -> " + msg + " : " + DATE);
                if (msg.equalsIgnoreCase("exit")) {
                    sendToServerScreen("Пользователь " + name + " отключен");
                    removeClient();
                    break;
                }
                sendToAll(msg);
                logInFile("Отправлено пользователям -> " + msg + " : " + DATE + "\n");
            } catch (IOException e) {
                closeAll(socket, in, out);
                break;
            }
        }
    }

    public void sendToAll(String s) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.name.equals(name)) {
                    clientHandler.out.write(s);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeAll(socket, in, out);
                break;
            }
        }
    }

    public void removeClient() {
        clientHandlers.remove(this);
        sendToAll("Пользователь " + name + " покинул чат. пользователей в чате " + --clientCount);
        logInFile("ПОЛЬЗОВАТЕЛЬ " + name + " ПОКИНУЛ ЧАТ. ПОЛЬЗОВАТЕЛЕЙ В ЧАТЕ " + clientCount + " : " + DATE);
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToServerScreen(String s) {
        System.out.println(s);
    }

    @Override
    public void logInFile(String msg) {
        new Thread(() -> {
            if (serverLog.exists()) {
                try {
                    inLogFile.write(msg);
                    inLogFile.newLine();
                    inLogFile.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}