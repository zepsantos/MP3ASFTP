package Client;


import MessageTypes.Message;
import MessageTypes.MessageAuthentication;
import MessageTypes.MessageTypes;
import MessageTypes.ResponseMessage;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader systemIn;
    private int userID;
    private String username;
    private String hostname;
    private int port;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.systemIn = new BufferedReader(new InputStreamReader(System.in));
    }

    private void connectServer() {
        if(this.socket == null) {
            connectSocket();
            return;
        }
       if(!this.socket.isConnected()) {
        connectSocket();
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

    public static void main(String[] args) {
        Client c = new Client("127.0.0.1", 12345);
        c.startClient();
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

    public void welcomeMenu() {
        System.out.print("\033[H\033[2J");
        System.out.println("---------Welcome!---------");
        System.out.println(" [1] Register");
        System.out.println(" [2] Login");
        System.out.println(" [3] Quit");
        System.out.println("--------------------------");
        System.out.print("Pick an option: ");
    }


    public void appMenu() {
        System.out.print("\033[H\033[2J");
        System.out.println("---------Welcome " + this.username);
        System.out.println(" [1] Upload da Musica");
        System.out.println(" [4] Logout");
        System.out.println("--------------------------");
    }



    private void startClient() {
        try {
            boolean quit = false;
            String op = null;
            welcomeMenu();
            while (!quit && (op = this.systemIn.readLine()) != null) {
                connectServer();
                switch (op) {
                    case "1":
                        write(new ResponseMessage(MessageTypes.ResponseMessage,-1,"register"));
                        System.out.println("--------- Register ---------");
                        userCycle("registered","Models.User already registered", MessageTypes.Register);
                        appStart();
                        break;
                    case "2":
                        write(new ResponseMessage(MessageTypes.ResponseMessage,-1,"login"));
                        System.out.println("------ Login -------");
                        userCycle("login done","Models.User logged in or not found",MessageTypes.Login);
                        appStart();
                        break;
                    default:
                        break;
                }
                close();
            }
        }catch(IOException e ) {
            e.printStackTrace();
        }
    }
    private void userCycle(String flag,String warning,MessageTypes type) {
        try {
            ResponseMessage response = null;
            String pass = null;

            do {
                System.out.println("Username:");
                this.username = this.systemIn.readLine();
                System.out.println("Password:");
                pass = this.systemIn.readLine();
                MessageAuthentication message = new MessageAuthentication(type,username,pass);
                write(message);
                String messageString = this.in.readLine();
                response = new ResponseMessage(messageString);
                if (response.getResponse() != null && response.getResponse().equals(flag)) {
                    this.userID = response.getUserID();
                } else
                    System.out.println(warning);
            } while (!response.getResponse().equals(flag));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void appStart() {
        try {
            boolean quit = false;
            String keyboard = null;
            while(!quit) {
                appMenu();
                while(!quit && (keyboard = this.systemIn.readLine()) != null) {
                    connectServer();
                    switch(keyboard) {
                        case "1":
                            break;
                        case "4":
                            write(new ResponseMessage(MessageTypes.ResponseMessage,userID,"logout"));
                            quit = true;
                            break;
                        default:
                            break;
                    }
                    close();
                }
            }
            welcomeMenu();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

}

/*

    }
     private void appStart() {
        try {
            boolean quit = false;
            String keyboard = null;
            while(!quit) {
                appMenu();
                while(!quit && (keyboard = this.systemIn.readLine()) != null) {
                    switch(keyboard) {
                        case "1":
                            break;
                        case "4":
                            write("logout");
                            quit = true;
                            break;
                        default:
                            break;
                    }
                }
            }
            welcomeMenu();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void startClient() {
        try {
            boolean quit=false;
            String keyboard = null;
            while(!quit) {
                welcomeMenu();
                while (!quit && (keyboard = this.systemIn.readLine()) != null) {
                    switch(keyboard) {
                        case "1":
                            write("register");
                            System.out.println("--------- Register ---------");
                            userCycle("registered","Models.User already registered");
                            appStart();
                            break;
                        case "2":
                            write("login");
                            System.out.println("------ Login -------");
                            userCycle("login done","Models.User logged in or not found");
                            appStart();
                            break;
                        default:
                            quit = true;
                            write("quit");
                    }

                }
            }

        } catch(IOException e) {

        }

    }
}

 */
