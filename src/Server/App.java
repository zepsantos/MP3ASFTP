package Server;

import Client.NotificationListener;
import Models.Music;
import Models.MusicDatabase;
import Models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class App {
    private HashMap<String, User> users;
    private HashMap<Integer, User> usersByIDSession;
    private NotificationAvailableListener notificationListener;
    private AtomicInteger lastID;
    private MusicDatabase musicDatabase;
    private static App inst = null;

    private App() {
        users = new HashMap<>();
        usersByIDSession = new HashMap<>();
        lastID = new AtomicInteger();
        musicDatabase = MusicDatabase.getInstance();
    }

    public static App getInstance() {
        if(inst == null) inst = new App();
        return inst;
    }

    public boolean registerUser(String username, String password) {
        if(!this.users.containsKey(username)) {
            User user = new User(username,password);
            users.put(username,user);
            return true;
        }
        return false;

    }

    public User loginUser(String username, String password) { //TODO: Ter cuidado com os locks
        if(this.users.containsKey(username)) {
            User user = this.users.get(username);
            if (user.getPassword().equals(password)) {
                int tmp = lastID.getAndIncrement();
                user.updateLoginID(tmp);
                this.usersByIDSession.put(tmp,user);
                return user;
            }
        }
        return null;
    }

    public void  logout(int uid) { //TODO: Ter cuidado com os locks
            this.usersByIDSession.remove(uid);
    }

    public void setNotificationListener(NotificationAvailableListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public void uploadMusic(Music music) {
        int tmp = musicDatabase.getLastMusicIDAndIncrement();
        music.setMusicID(tmp);
        musicDatabase.put(tmp,music);
        if(this.notificationListener != null){
            this.notificationListener.broadcastMusicNotification(music);
        }

    }

    public List<Music> getMusicsList() {
        return (List<Music>) musicDatabase.values().stream().collect(Collectors.toList());
}




}
