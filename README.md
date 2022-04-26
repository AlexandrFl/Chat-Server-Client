## Сетевой чат 
![](https://icons.iconarchive.com/icons/icons8/windows-8/128/Messaging-Message-Group-icon.png)

Сетевой чат состоит из следующих классов:

1. _Server_;
2. _ClientHandler_;
3. _Client_;
4. _ServerMain_;
5. _ClientMain_;


_**1. Класс Server**_

   Класс Server имеет конструктор:

```java
public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}
```
Метод _startServer()_ ожидает подключения к сокету. После того как подключение произошло, создается экземпляр класса _ClientHandler_, который имплементируют интерфейс _Runnable_. Реализация этого экземпляра происходит в отдельном потоке, что позволяет серверы обрабатывать несколько клиентов.

**_2. Класс ClientHandler_**

Класс имеет конструктор:

```java
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
}
```
Этот класс принимает строку от пользователя и передает ее остальным пользователям из списка.

Класс имеет следующие методы;
1. Переопределенный метод _run()_. 
В нем заложен основной функционал класса. Входной поток принимает строку от клиента. Затем происходит ее обработка (_equalsIgnoreCase()_) и с помощью _BufferedWriter_ строка передается другим участникам сессии.
2. Метод _sendToAll()_. Необходим для отправки сообщения всем пользователям, кроме самого автора сообщения.
3. Метод _removeClient()_. Используется для удаления клиентов из списка подключенных пользователей.
4. Метод _logInFile()_. Используется для записи статуса клиентов, а так же сообщений входящих на сервер и исходящих из него. Логирование производится в файл _ServerLog.txt_.


**_3. Класс Client_**

Класс имеет конструктор:

```java
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
}
```

Класс необходим для реализации логики клиента и имеет следующие методы:

1. метод _sengMsg()_. Считывает строку, которую вводит пользователь с помощью _Scanner_ и отправляет ее с помощью _BufferedWriter_.
2. метод _getMsg()_. Используется для получения строки от сервера с помощью _BufferedReader_. Так как метод _readLine()_ в методе _sendMsg()_ блокирующий, реализация метода _getMsg()_ вынесена в отдельный поток для одновременной возможности принимать и отправлять сообщения.


_**4. Классы ServerMain и ClientMain**_

Используются для запуска сервера и клиента. Их основной метод readSettingFile() используется для получения host и port из файла настроек _setting_.txt