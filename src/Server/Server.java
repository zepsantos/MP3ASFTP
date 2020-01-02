package Server;

import MessageTypes.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.*;


public class Server {
    private ServerSocket serverSocket;
    private int port;
    private App app;
    private ThreadPoolExecutor threadPoolExecutor;

    public Server(int port) {
        this.port = port;
        app = App.getInstance();
        threadPoolExecutor = new ThreadPoolExecutor(10, 10, 1000, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                threadPoolExecutor.execute(runnable);
            }
        });
    }

    private void startServer() {
        try {
            System.out.println("####### SERVER #######");
            this.serverSocket = new ServerSocket(this.port);
            while(true) {
                System.out.println("Waiting for connection...");
                Socket socket=serverSocket.accept();
                System.out.println("Client connected. Starting thread.");
                String op = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                op = in.readLine();
                if(op != null) { //TODO: substituir por while caso queiramos passar mais do que uma mensagem
                    Message tmp = getMessageObject(op);
                    if (tmp.isValidMessage())
                        if(tmp instanceof Notification) {

                        } else {
                            threadPoolExecutor.execute(new ServerWorkerFutureTask(new ServerWorker(new MessageConnection(tmp,socket))));
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

   /* private void teste() {

        ServerWorker serverWorker = new ServerWorker(Objects.requireNonNull(messageQueue.poll()),app);
        executorService.submit(serverWorker);


    } */
}
