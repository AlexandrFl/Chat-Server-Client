package testClient;

import Client.ClientMain;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.ArrayDeque;
import java.util.Date;

public class testClientMain {
    private static final Date DATE = new Date();

    @BeforeAll
    public static void start() {
        System.out.println("Тестирование началось " + DATE);
    }

    @AfterAll
    public static void finish() {
        System.out.println("Тестирование завершено " + DATE);
    }

    @Test
    public void testReadSettingFile() {
        String realHost = "127.0.0.1";
        int realPort = 9090;
        ArrayDeque<String> settings = ClientMain.readSettingFile();
        assertEquals(2, settings.size());
        assertEquals(realHost, settings.pollFirst());
        assertEquals(realPort, Integer.parseInt(settings.pollFirst()));
    }
}
