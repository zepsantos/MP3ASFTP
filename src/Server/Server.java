package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private ServerSocket serverSocket;
    private int port;
    private App app;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    public Server(int port) {
        this.port = port;
        app = new App();
    }

    private void startServer() {
        try {
            System.out.println("####### SERVER #######");
            this.serverSocket = new ServerSocket(this.port);
            while(true) {
                System.out.println("Waiting for connection...");
                Socket socket=serverSocket.accept();
                System.out.println("Client connected. Starting thread.");
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
