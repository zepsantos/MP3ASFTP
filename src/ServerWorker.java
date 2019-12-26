import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerWorker implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private App app;
    private User user;
    public ServerWorker(Socket socket,App app) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.app = app;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String msg) {
        try {
            this.out.write(msg);
            this.out.newLine();
            this.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String op = null;
            boolean repeat = true;
            String username = null;
            String password = null;
            while ((op = this.in.readLine()) != null) {
                switch(op) {
                    case "register":
                        do {
                            username = this.in.readLine();
                            password = this.in.readLine();
                            if (app.registerUser(username, password)) {
                                write("registered");
                                user = app.loginUser(username, password);
                                repeat = false;
                            } else
                                write("not registered");
                        } while(repeat);
                        break;
                    case "login":
                        do {
                            username = this.in.readLine();
                            password = this.in.readLine();
                            if ((user = app.loginUser(username, password)) != null) {
                                write("login done");
                                repeat = false;
                            } else {
                                write("login unsuccessful");
                                op = "login";
                            }
                        }while(repeat);
                        break;
                    case "logout":
                        this.app.logout(user.getUsername());
                        break;
                }
            }
        } catch (IOException ioe) {
            try {
                System.out.println("Worker-" + Thread.currentThread().getId() + " > Client disconnected. Connection is closed.");
                close();
            } catch (Exception e) {
                System.out.println("ERROR2: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }
}
