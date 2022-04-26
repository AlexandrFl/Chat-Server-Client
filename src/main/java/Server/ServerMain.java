package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayDeque;

public class ServerMain {

    private static String host;
    private static int port;

    public static void main(String[] args) throws IOException {
        ArrayDeque<String> settings = readSettingFile();
        if (settings.size() == 2) {
            host = settings.pollFirst();
            port = Integer.parseInt(settings.pollFirst());
            System.out.println("Соединение установлено\nHOST : " + host);
            System.out.println("PORT : " + port);

            readSettingFile();
            ServerSocket serverSocket = new ServerSocket(port);
            Server server = new Server(serverSocket);
            server.startServer();
        } else {
            System.out.println("Файл настроек не содержит всю необходимую информацию");
        }
    }

    public static ArrayDeque<String> readSettingFile() {
        ArrayDeque<String> settings = new ArrayDeque<>();
        File setting = new File("D:\\Java Project\\Chat\\setting.txt");
        if (setting.exists()) {
            try {
                try (BufferedReader in = new BufferedReader(new FileReader(setting))) {
                    String s;
                    while ((s = in.readLine()) != null) {
                        if (s.startsWith("host")) {
                            host = s.substring(s.indexOf(':') + 1, s.indexOf(';'));
                            settings.add(host);
                        } else if (s.startsWith("port")) {
                            String subPort = s.substring((s.indexOf(':') + 1), s.indexOf(';'));
                            settings.add(subPort);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Файла настроек отсутствует");
        }
        return settings;
    }
}
