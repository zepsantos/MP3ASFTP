package Server;

import Models.MusicDatabase;
import Models.User;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class App {
    private HashMap<String, User> users;
    private HashMap<Integer, User> usersByIDSession;
    private AtomicInteger lastID;
    private MusicDatabase musicDatabase;
    private static App inst = null;
    private ReentrantLock lock;

    public App() {
        this.lock = new ReentrantLock();
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
        this.lock.lock();
        if(!this.users.containsKey(username)) {
            User user = new User(username,password);
            users.put(username,user);
            this.lock.unlock();
            return true;
        }
        this.lock.unlock();
        return false;

    }

    public User loginUser(String username, String password) { //TODO: Ter cuidado com os locks
        this.lock.lock();
        if(this.users.containsKey(username)) {
            User user = this.users.get(username);
            if (user.getPassword().equals(password)) {
                int tmp = lastID.getAndIncrement();
                user.updateInt(tmp);
                this.usersByIDSession.put(tmp,user);
                this.lock.unlock();
                return user;

            }
        }
        this.lock.unlock();
        return null;
    }

    public void  logout(int uid) { //TODO: Ter cuidado com os locks
        this.lock.lock();
            this.usersByIDSession.remove(uid);
        this.lock.unlock();
    }




}
