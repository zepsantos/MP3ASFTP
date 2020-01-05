package Server;

import Models.Music;
import Models.MusicDatabase;
import Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class App {
    private HashMap<String, User> users;
    private HashMap<Integer, User> usersByIDSession;
    private NotificationAvailableListener notificationListener;
    private AtomicInteger lastID;
    private MusicDatabase musicDatabase;
    private ReentrantLock tagsLock;
    private HashMap<String,List<Integer>> tagsMap;
    private static App inst = null;

    private App() {
        users = new HashMap<>();
        usersByIDSession = new HashMap<>();
        tagsMap = new HashMap<>();
        lastID = new AtomicInteger();
        tagsLock = new ReentrantLock();
        musicDatabase = MusicDatabase.getInstance();
    }

    public static App getInstance() {
        if(inst == null) {
            ReentrantLock lock = new ReentrantLock();
            try {
                lock.lock();
                inst = new App();
            } finally {
                lock.unlock();
            }

        }

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

    public void  logout(int uid) {
            this.usersByIDSession.remove(uid);
    }

    public void setNotificationListener(NotificationAvailableListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public void uploadMusic(Music music) {
        musicDatabase.put(-1,music);
        for(String tag : music.getTags()) {
            try {
                tagsLock.lock();
                List<Integer> list = tagsMap.computeIfAbsent(tag, k -> new ArrayList<>());
                list.add(music.getMusicID());
            } finally {
                    tagsLock.unlock();
                }
            }


        if(this.notificationListener != null){
            this.notificationListener.broadcastMusicNotification(music);
        }

    }

    public List<Music> getMusicsList() {
        return (List<Music>) musicDatabase.values().stream().collect(Collectors.toList());
}


    public List<Music> getMusicsList(String tag) {
        try {
            tagsLock.lock();

            if (this.tagsMap.containsKey(tag)) {
                List<Integer> list = this.tagsMap.get(tag);
                List<Music> musicList = new ArrayList<>();
                for (Integer i : list) {
                    musicList.add(this.musicDatabase.get(i));
                }
                return musicList;
            }
        } finally {
            tagsLock.unlock();
        }
        return new ArrayList<>();
    }


}
