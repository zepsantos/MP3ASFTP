package Server;

import MessageTypes.Message;
import MessageTypes.MessageAuthentication;
import MessageTypes.MessageTypes;
import MessageTypes.ResponseMessage;
import Models.User;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerWorker implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private App app;
    private User user;
    public ServerWorker(Socket socket, App app) {
        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.app = app;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(Message msg) {
        try {
            this.out.write(msg.toString());
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
            op = this.in.readLine();
            if(op != null) {
                ResponseMessage responseMessage = new ResponseMessage(op);
                switch (responseMessage.getResponse()) {
                    case "register":
                        do {
                            MessageAuthentication messageAuthentication = new MessageAuthentication(this.in.readLine());
                            username = messageAuthentication.getUser();
                            password = messageAuthentication.getPassword();
                            if (app.registerUser(username, password)) {
                                user = app.loginUser(username, password);
                                write(new ResponseMessage(MessageTypes.ResponseMessage, user.getID(), "registered"));
                                repeat = false;
                            } else
                                write(new ResponseMessage(MessageTypes.ResponseMessage, -1, "not registered"));
                        } while (repeat);
                        break;
                    case "login":
                        do {
                            MessageAuthentication messageAuthentication = new MessageAuthentication(this.in.readLine());
                            if ((user = app.loginUser(messageAuthentication.getUser(), messageAuthentication.getPassword())) != null) {
                                write(new ResponseMessage(MessageTypes.ResponseMessage, user.getID(), "login done"));
                                repeat = false;
                            } else {
                                write(new ResponseMessage(MessageTypes.ResponseMessage, -1, "login unsuccessful"));
                            }
                        } while (repeat);
                        break;
                    case "music upload":

                        break;
                    case "logout":
                        this.app.logout(responseMessage.getUserID());
                        break;
                }
            }
        } catch (IOException ioe) {
            try {
                System.out.println("Worker-" + Thread.currentThread().getId() + " > Client.Client disconnected. Connection is closed.");
                close();
            } catch (Exception e) {
                System.out.println("ERROR2: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            close();
        }


    }
}
