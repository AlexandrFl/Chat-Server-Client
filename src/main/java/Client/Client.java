package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private String name;
    private BufferedReader in;
    private BufferedWriter out;

    public Client(Socket socket, String name) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = name;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.write(name);
            out.newLine();
            out.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String msg = scanner.nextLine();
                if (msg.equalsIgnoreCase("exit")) {
                    out.write(msg);
                } else {
                    out.write(name + ": " + msg);
                }
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeAll(socket, in, out);
        }
    }

    public void getMsg() {
        new Thread(() -> {
            String msg;
            while (socket.isConnected()) {
                try {
                    msg = in.readLine();
                    System.out.println(msg);
                    if (msg == null) {
                        break;
                    }
                } catch (IOException e) {
                    closeAll(socket, in, out);
                    break;
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}