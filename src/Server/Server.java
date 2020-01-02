package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;


public class Server {
    private ServerSocket serverSocket;
    private int port;
    private App app;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Socket[] notificationsBroadcast;

    public Server(int port) {
        this.port = port;
        app = new App();
        LOGGER.setLevel(Level.INFO);
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        ch.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";
            @Override
            public String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        });
        LOGGER.addHandler(ch);
    }

    private void startServer() {
        try {
            LOGGER.info("Inicializing Server...");
            this.serverSocket = new ServerSocket(this.port);
            while(true) {
                LOGGER.info("Waiting for Connection");
                Socket socket=serverSocket.accept();
                LOGGER.info("Client Request.");
                ServerWorker serverWorker = new ServerWorker(socket,app);
                executorService.submit(serverWorker);
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Server server = new Server(12345);
        server.startServer();

    }
}
