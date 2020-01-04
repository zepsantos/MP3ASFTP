package Server;

import MessageTypes.*;

import Models.Music;
import Models.MusicDatabase;
import Models.User;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;


public class ServerWorker implements Runnable {

    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private MessageConnection messageConnection;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private App app;
    private User user;

    public ServerWorker(MessageConnection messageConnection) {
        try {
            this.messageConnection = messageConnection;
            this.socket = messageConnection.getSocket();
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.app = App.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MessageConnection getMessageConnection() {
        return messageConnection;
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

                MessageTypes type = messageConnection.getMessage().getMessageType();
                switch (type) {
                    case Register:
                    case Login:
                        authenticationProcess(type);
                        break;
                    case MP3Upload:
                        mp3Download();
                        break;
                    case MP3Download:
                        MP3Download mp3Download = (MP3Download) getMessageConnection().getMessage();
                        Music mtmp = MusicDatabase.getInstance().get(mp3Download.getIdMusic());
                        if(mtmp == null) return;
                        write(new ResponseMessage(-1,mtmp.getFilePath()));
                        mp3Upload(mtmp);
                        break;
                    case ResponseMessage:
                        ResponseMessage responseMessage = (ResponseMessage) messageConnection.getMessage();
                        if(responseMessage.getResponse().equals("logout")) {
                            logoutUser(responseMessage.getUserID());
                        }else if(responseMessage.getResponse().substring(0,9).equals("musicList")){
                            String[] responseTag = responseMessage.getResponse().split(";");
                            if(responseTag.length == 2)
                                sendMusicList(responseTag[1]);
                            else
                                sendMusicList();
                        }
                        break;
                }
                close();

    }

    private void sendMusicList() {
        MusicListMessage musicListMessage = new MusicListMessage(this.app.getMusicsList());
        write(musicListMessage);
        log.info("Lista de musicas enviada para " + this.socket.getInetAddress().getHostAddress());
    }
    private void sendMusicList(String tag) {
        List<Music> tmpMusicList = this.app.getMusicsList(tag);
        MusicListMessage musicListMessage = new MusicListMessage(tmpMusicList);
        write(musicListMessage);
        log.info("Lista de musicas com a tag " + tag +  " enviada para " + this.socket.getInetAddress().getHostAddress());
    }

    private void logoutUser(int userID) {
        log.info(userID+ " logged out");
        this.app.logout(userID);
    }

    private void authenticationProcess(MessageTypes type) {
        boolean repeat = true;
        String username = null;
        String password = null;
        do {
            MessageAuthentication messageAuthentication = (MessageAuthentication)messageConnection.getMessage();
            username = messageAuthentication.getUser();
            password = messageAuthentication.getPassword();
            if(type == MessageTypes.Register ) {
                if (app.registerUser(username, password)) {
                    user = app.loginUser(username, password);
                    write(new ResponseMessage(user.getID(), "registered"));
                    log.info(username + " registered");
                    repeat = false;
                } else {
                    write(new ResponseMessage(-1, "not registered"));
                }
            } else {
                if((user = app.loginUser(username,password)) != null) {
                    write(new ResponseMessage(user.getID(), "login done"));
                    log.info(username + " logged in with id: " + user.getID());
                    repeat = false;
                } else {
                    write(new ResponseMessage( -1, "login unsuccessful"));
                }

            }
        } while (repeat);
    }

    private void mp3Upload(Music music) {
        try {
            DataTransfer dataTransfer = new DataTransfer(this.socket);
            dataTransfer.UploadFile(music.getFilePath());
            log.info("Downloaded music: " + music.getTitle());
        } catch(IOException e) {
            log.warning("Failed to upload music to client");
        }

    }

    private void mp3Download() {
        try {
            MP3Upload mp3Upload = (MP3Upload)getMessageConnection().getMessage();
            DataTransfer dataTransfer = new DataTransfer(this.socket);
            dataTransfer.DownloadFile(mp3Upload.getFileName());
            log.info("Uploaded music: " + mp3Upload.getFileName());
            Music tmp = (Music) mp3Upload.getMusic().clone();
            tmp.setOwnerOfUploadID(mp3Upload.getIdUser());
            app.uploadMusic(tmp);
        }catch(IOException e) {
            log.warning("Failed to download music from client");
        }
    }

}
