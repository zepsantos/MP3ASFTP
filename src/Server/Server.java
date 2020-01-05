package Server;

import MessageTypes.*;
import Models.Music;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;


public class Server implements NotificationAvailableListener, DownloadFinishedListener {
    public static int MAX_DOWNLOAD_SAMETIME = 1;
    private ServerSocket serverSocket;
    private int port;
    private ThreadPoolExecutor threadPoolExecutor;
    private List<MessageConnection> connectionList;
    private AtomicInteger downloadsAtTheSameTime;
    private final static Logger  log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private Queue<MessageConnection> mp3DownloadQueue;
    private HashMap<Integer, Integer> nDownloadsPerID;
    private App app;
    public Server(int port) {
        this.port = port;
        this.app = App.getInstance();
        instantiateMaxDownload();
        connectionList = new ArrayList<>();
        threadPoolExecutor = new ThreadPoolExecutor(2, 3, 1000, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(), new RejectedExecutionHandler() {
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

    private void instantiateMaxDownload() {
        this.nDownloadsPerID = new HashMap<>();
        this.downloadsAtTheSameTime = new AtomicInteger();
        this.mp3DownloadQueue = new PriorityQueue<MessageConnection>(new Comparator<MessageConnection>() {
            @Override
            public int compare(MessageConnection mc1, MessageConnection mc2) {
                MP3Download m1 = (MP3Download) mc1.getMessage();
                MP3Download m2 = (MP3Download) mc2.getMessage();
                int countm1 = nDownloadsPerID.get(m1.getIdUser());
                int countm2 = nDownloadsPerID.get(m2.getIdUser());
                return countm1 - countm2;
            }
        });
        DownloadsFinisherHelper.getInstance().setDownloadFinishedListener(this);
    }

    private void startServer() {
        app.setNotificationListener(this);
        new Thread(new DownloadQueueWorker(mp3DownloadQueue,this.downloadsAtTheSameTime,this.threadPoolExecutor,this.nDownloadsPerID));
        try {
            log.info("Server inicializing...");
            this.serverSocket = new ServerSocket(this.port);
            log.info("Waiting for Connection");
            while(true) {

                Socket socket=serverSocket.accept();
                String op = null;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                op = in.readLine();
                if(op != null) {
                    Message tmp = getMessageObject(op);
                    if (tmp.isValidMessage()) {
                        log.info(new StringBuilder().append("Message Type: ").append(tmp.getMessageType().toString()).append(" received from ").append(socket.getInetAddress().getHostAddress()).toString());
                        if (tmp instanceof Notification) {
                            processNotification(socket, tmp);
                        } else if (tmp instanceof MP3Download) {
                            processMP3Download(socket, tmp);
                        } else {
                            threadPoolExecutor.execute(new ServerWorkerFutureTask(new ServerWorker(new MessageConnection(tmp, socket))));
                        }

                    }
                }
            }

        } catch (IOException ex) {
            log.warning("Servidor provavelmente foi a baixo");
        }
    }



    private void processMP3Download(Socket socket, Message message) {
        if (this.downloadsAtTheSameTime.get() == MAX_DOWNLOAD_SAMETIME) {
            int userID = ((MP3Download) message).getIdUser();
            Integer tmpCount = this.nDownloadsPerID.get(userID);
            if (tmpCount == null)
                tmpCount = 0;
            this.nDownloadsPerID.put(userID, ++tmpCount);
            this.mp3DownloadQueue.add(new MessageConnection(message, socket));
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                bufferedWriter.write(new ResponseMessage(userID, "onQueue").toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                log.warning("Erro ao avisar cliente que o download estava em lista de espera");
            }
            log.info("MP3Download added to queue");
        } else {
            runMP3DownloadWorker(new MessageConnection(message, socket));
        }
    }

    private void runMP3DownloadWorker(MessageConnection messageConnection) {
        threadPoolExecutor.execute(new ServerWorkerFutureTask(new ServerWorker(messageConnection)));
        this.downloadsAtTheSameTime.incrementAndGet();
    }

    private void processNotification(Socket socket, Message message) {
        log.info("Added to notification broadcast " + socket.getInetAddress().getHostAddress());
        this.connectionList.add(new MessageConnection(message, socket));
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
            case MusicList:
                return new MusicListMessage(op);
            case MP3Download:
                return new MP3Download(op);
        }
        return new ResponseMessage("");
    }

    public static void main(String[] args) {
        Server server = new Server(12345);
        server.startServer();

    }

    @Override
    public void broadcastMusicNotification(Music m)  {
        for(MessageConnection messageConnection : connectionList) {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(messageConnection.getSocket().getOutputStream()));
                ResponseMessage tmp  = new ResponseMessage(m.getOwnerOfUploadID(),m.getTitle());
                bw.write(tmp.toString());
                bw.newLine();
                bw.flush();
                log.info("Sending Notification to " + messageConnection.getSocket().getInetAddress().getHostAddress());
            } catch(IOException e) {
                log.severe("Failed to send notification to " + messageConnection.getSocket().getInetAddress().getHostAddress());
            }
        }
    }

    public void decrementDownloadsAtTheSameTime() {
        this.downloadsAtTheSameTime.getAndDecrement();
    }


}
