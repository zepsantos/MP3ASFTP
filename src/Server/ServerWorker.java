package Server;

import MessageTypes.Message;
import MessageTypes.MessageAuthentication;
import MessageTypes.MessageTypes;
import MessageTypes.MP3Upload;
import MessageTypes.ResponseMessage;
import Models.User;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static MessageTypes.MessageTypes.MP3Upload;

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
            if(!socket.isClosed()) {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String op = null;
            op = this.in.readLine();
            if(op != null) {
                MessageTypes type = Message.getMessageType(op);
                switch (type) {
                    case Register:
                    case Login:
                        authenticationProcess(op,type);
                        break;
                    case MP3Upload:
                        mp3BeginUpload(op);
                        break;
                    case ResponseMessage:
                        ResponseMessage responseMessage = new ResponseMessage(op);
                        this.app.logout(responseMessage.getUserID());
                        break;
                }
            }
        } catch (IOException ioe) {
            try {
                System.out.println("Worker-" + Thread.currentThread().getId() + " > Client disconnected. Connection is closed.");
            } catch (Exception e) {
                System.out.println("ERROR2: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            close();
        }


    }

    private void authenticationProcess(String message,MessageTypes type) {
        boolean repeat = true;
        String username = null;
        String password = null;
        do {
            MessageAuthentication messageAuthentication = new MessageAuthentication(message);
            username = messageAuthentication.getUser();
            password = messageAuthentication.getPassword();
            if(type == MessageTypes.Register ) {
                if (app.registerUser(username, password)) {
                    user = app.loginUser(username, password);
                    write(new ResponseMessage(MessageTypes.ResponseMessage, user.getID(), "registered"));
                    repeat = false;
                } else
                    write(new ResponseMessage(MessageTypes.ResponseMessage, -1, "not registered"));
            } else {
                if((user = app.loginUser(username,password)) != null) {
                    write(new ResponseMessage(MessageTypes.ResponseMessage, user.getID(), "login done"));
                    repeat = false;
                } else {
                    write(new ResponseMessage(MessageTypes.ResponseMessage, -1, "login unsuccessful"));
                }

            }
        } while (repeat);
    }

    private void mp3BeginUpload(String message) {
        try {
            MP3Upload mp3Upload = new MP3Upload(message);
            DataInputStream dis = new DataInputStream(this.socket.getInputStream());
            BufferedInputStream input = new BufferedInputStream(dis);
            System.out.println("NOME DO FICHEIRO: " + mp3Upload.getFileName());
            OutputStream outputFile = new FileOutputStream("uploaded"); //TODO : DAR UPLOAD COM NOME
            long size = dis.readLong();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                outputFile.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            outputFile.close();
            dis.close();
        }catch(IOException e) {
            e.printStackTrace();
        }

    }
}
