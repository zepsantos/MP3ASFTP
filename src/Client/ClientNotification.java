package Client;

import MessageTypes.*;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientNotification implements Runnable {
    private NotificationListener listener;
    private AtomicBoolean running = new AtomicBoolean(false);
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String hostname;
    private int port;
    private int userID;

    public ClientNotification(NotificationListener listener,String hostname,int port,int userID) {
        this.listener = listener;
        this.hostname = hostname;
        this.port = port;
        this.userID = userID;
    }

    private void close() {
        try {
            if(this.socket == null) return;
            this.socket.close();
            this.in.close();
            this.out.close();
            this.socket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(Message msg) {
        try {
            this.out.write(msg.toString());
            this.out.newLine();
            this.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void connectSocket() {
        try {
            this.socket = new Socket(hostname, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        connectSocket();
        write(new Notification(userID));
        running.set(true);
        while(running.get()) {
            try {
                if(!this.in.ready()) {
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e) {
                        running.set(false);
                        close();
                    }
                }
                ResponseMessage notification = new ResponseMessage(this.in.readLine());
                if(notification.getResponse() == null) continue;
                StringBuilder sb = new StringBuilder("O utilizador ");
                sb.append(notification.getUserID());
                sb.append(" fez upload da musica ");
                sb.append(notification.getResponse());
                listener.showMusicUploadNotification(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       close();
    }
}
