import java.io.*;
import java.net.Socket;

public class Client  {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader systemIn;
    private String username;

    public Client(String hostname,int port) {
        try{
            this.socket=new Socket(hostname,port);
            this.in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.systemIn=new BufferedReader(new InputStreamReader(System.in));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client c = new Client("127.0.0.1",12345);
        c.startClient();
    }

    public void write(String msg){
        try{
            this.out.write(msg);
            this.out.newLine();
            this.out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void welcomeMenu(){
        System.out.print("\033[H\033[2J");
        System.out.println("---------Welcome!---------");
        System.out.println(" [1] Register");
        System.out.println(" [2] Login");
        System.out.println(" [3] Quit");
        System.out.println("--------------------------");
        System.out.print("Pick an option: ");
    }

    private void userCycle(String flag,String warning) {
        try {
            String response = null;
            String pass = null;

            do {
                System.out.println("Username:");
                this.username = this.systemIn.readLine();
                write(username);
                System.out.println("Passowrd:");
                pass = this.systemIn.readLine();
                write(pass);
                response=this.in.readLine();
                if(!response.equals(flag))
                    System.out.println(warning);
            }while(!response.equals(flag));
        }catch(IOException e) {
            e.printStackTrace();
        }


    }

    public void appMenu(){
        System.out.print("\033[H\033[2J");
        System.out.println("---------Welcome "+this.username);
        System.out.println(" [1] Upload da Musica");
        System.out.println(" [4] Logout");
        System.out.println("--------------------------");
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
                            userCycle("registered","User already registered");
                            appStart();
                            break;
                        case "2":
                            write("login");
                            System.out.println("------ Login -------");
                            userCycle("login done","User logged in or not found");
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
