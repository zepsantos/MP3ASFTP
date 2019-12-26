import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private int port;
    private App app;
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
                new Thread(serverWorker).start();
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
