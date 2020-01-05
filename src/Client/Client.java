package Client;


import MessageTypes.*;
import Models.Music;
import Server.DataTransfer;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader systemIn;
    private int userID;
    private String username;
    private String hostname;
    private int port;
    private NotificationListener notificationListener;
    private Thread notificationThread;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.notificationListener = notification -> System.out.println(notification);
        this.systemIn = new BufferedReader(new InputStreamReader(System.in));
    }

    private void connectServer() {
        connectSocket();
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
        System.out.println("--------- Welcome " + this.username + " with id " + this.userID + " ---------");
        System.out.println(" [1] Upload da Musica");
        System.out.println(" [2] Lista de Musicas");
        System.out.println(" [3] Lista de Musicas com uma tag");
        System.out.println(" [4] Download de Musica por id");
        System.out.println(" [5] Logout");
        System.out.println("--------------------------");
    }



    private void startClient() {
        try {
            boolean quit = false;
            String op = null;
            welcomeMenu();
            while (!quit && (op = this.systemIn.readLine()) != null) {

                switch (op) {
                    case "1":
                        connectServer();
                        System.out.println("--------- Register ---------");
                        userCycle("registered","User already registered", MessageTypes.Register);
                        close();
                        appStart();
                        break;
                    case "2":
                        connectServer();
                        System.out.println("------ Login -------");
                        userCycle("login done","User not found",MessageTypes.Login);
                        close();
                        appStart();
                        break;
                    default:
                        quit = true;
                        break;
                }

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
            listenForNotifications();
            while(!quit) {
                appMenu();
                while(!quit && (keyboard = this.systemIn.readLine()) != null) {
                    switch(keyboard) {
                        case "1":
                            processUploadMusic();
                            break;
                        case "2":
                            connectServer();
                            write(new ResponseMessage(userID,"musicList"));
                            listenForMusicListAndPrintIT();
                            close();
                            this.systemIn.readLine();
                            break;
                        case "3":
                            connectServer();
                            System.out.println("Insira a tag:");
                            String tag = this.systemIn.readLine();
                            write(new ResponseMessage(userID,"musicList;" + tag));
                            listenForMusicListAndPrintIT();
                            close();
                            this.systemIn.readLine();
                            break;
                        case "4":
                            connectServer();
                            System.out.println("Insira o id da musica:");
                            int idM = Integer.parseInt(this.systemIn.readLine());
                            write(new MP3Download(userID,idM));
                            ResponseMessage messageWithFileName = new ResponseMessage(this.in.readLine());
                            if (!messageWithFileName.getResponse().equals("fileNotFound")) {
                                new Thread(() -> {
                                    DataTransfer dataTransfer = new DataTransfer(socket);
                                    try {
                                        dataTransfer.DownloadFile(addClientPath(messageWithFileName.getResponse()));
                                        notificationListener.showMusicUploadNotification("Download da musica com o ficheiro: " + messageWithFileName.getResponse() + " concluido");
                                    } catch (IOException e) {
                                        notificationListener.showMusicUploadNotification("Falha no download da musica");
                                    } finally {
                                        close();
                                    }

                                }).start();
                            } else {
                                System.out.println("Não existe esse id");
                                close();
                            }
                            break;
                        case "5":
                            connectServer();
                            write(new ResponseMessage(userID,"logout"));
                            quit = true;
                            notificationThread.interrupt();
                            close();
                            break;
                        default:
                            break;
                    }
                    appMenu();
                }
            }
            welcomeMenu();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void listenForMusicListAndPrintIT() throws IOException{ //TODO: THREADS A ESTOURAR AQUI TALVEZ
        String tmp = this.in.readLine();
        MusicListMessage musicListMessage = new MusicListMessage(tmp);
        printMusicList(musicListMessage.getMusicList());
    }


    private void processUploadMusic() throws IOException {
        connectServer();
        String mp3FileName = chooseMp3File();
        Music music = getMusicInfoFromUser();
        if (music != null) {
            music.setFilePath(mp3FileName);
            MP3Upload mp3Message = new MP3Upload(userID, music);
            write(mp3Message);
            DataTransfer dataTransfer = new DataTransfer(this.socket);
            new Thread(() -> {
                try {
                    dataTransfer.UploadFile(mp3FileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    notificationListener.showMusicUploadNotification("Falha no upload da musica");
                } finally {
                    close();
                }
            }).start();
        }
    }

    private void printMusicList(List<Music> musicList) {
        System.out.println("ID     Titulo               Artista              Ano     NDownload   UploadID");
        if(musicList.isEmpty()) System.out.println("Nao ha musicas carregadas");
        for(Music m : musicList) {
            if(m == null) continue;
            StringBuilder sb = new StringBuilder();
            sb.append(m.getMusicID());
            sb.append("       ").append(m.getTitle());
            sb.append("              ").append(m.getArtist());
            sb.append("              ").append(m.getYear());
            sb.append("              ").append(m.getnTimesMusicHasBeenDownloaded());
            sb.append("              ").append(m.getOwnerOfUploadID());
            System.out.println(sb.toString());
        }
    }

    private void listenForNotifications() {
        ClientNotification clientNotification = new ClientNotification(notificationListener
                , hostname, port, userID);
        notificationThread = new Thread(clientNotification);
        notificationThread.start();

    }


    private Music getMusicInfoFromUser() {
        try {
            System.out.println("Insira o nome da musica: ");
            String title = this.systemIn.readLine();
            System.out.println("Insira o nome do artista: ");
            String artist = this.systemIn.readLine();
            System.out.println("Insira o ano da musica: ");
            String year = this.systemIn.readLine();
            System.out.println("Insira as tags da musica (separado por ;) : ");
            String tagString = this.systemIn.readLine();
            Music tmp = new Music(title,artist,year,null);
            String[] tags = tagString.split(";");
            for(String s : tags) tmp.addTag(s);
            return tmp;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String chooseMp3File() throws IOException {
        File tmp = null;
        String path;

        do {
            System.out.println("Insira o path do ficheiro: ");
            path = this.systemIn.readLine();
            tmp = new File(path);

        } while (!tmp.exists());
        return path;
    }


    private String addClientPath(String s) {
        return "ClientFiles/" + s;
    }
}


