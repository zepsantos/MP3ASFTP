package Server;

import MessageTypes.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.*;


public class Server {
    private ServerSocket serverSocket;
    private int port;
    private App app;
    private ThreadPoolExecutor threadPoolExecutor;
    private final static Logger  log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public Server(int port) {
        this.port = port;
        app = App.getInstance();
        threadPoolExecutor = new ThreadPoolExecutor(10, 10, 1000, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                threadPoolExecutor.execute(runnable);
            }
        });
        log.setLevel(Level.ALL);
        log.setUseParentHandlers(false);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(new SimpleFormatter() {

            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()

                );
        }});
        log.addHandler(ch);
    }

    private void startServer() {
        try {
            log.info("Server inicializing...");
            this.serverSocket = new ServerSocket(this.port);
            log.info("Waiting for Connection");
            while(true) {
                Socket socket=serverSocket.accept();
                String op = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                op = in.readLine();
                if(op != null) { //TODO: substituir por while caso queiramos passar mais do que uma mensagem
                    Message tmp = getMessageObject(op);

                    if (tmp.isValidMessage()) {
                        log.fine(new StringBuilder().append("Message Type: ").append(tmp.getMessageType().toString()).append(" received from ").append(socket.getInetAddress().getHostAddress()).toString());
                        if (tmp instanceof Notification) {
                            socket.close();
                        } else {
                            threadPoolExecutor.execute(new ServerWorkerFutureTask(new ServerWorker(new MessageConnection(tmp, socket))));
                        }
                    }
                    }
                }

            } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Message getMessageObject(String op ) {
        MessageTypes type = Message.getMessageType(op);
        switch(type) {
            case Login:
            case Register:
                return new MessageAuthentication(op);
            case ResponseMessage:
                return new ResponseMessage(op);
            case MP3Upload:
                return new MP3Upload(op);
            case Notification:
                return new Notification(op);
        }
        return new ResponseMessage("");
    }

    public static void main(String[] args) {
        Server server = new Server(12345);
        server.startServer();

    }


}
