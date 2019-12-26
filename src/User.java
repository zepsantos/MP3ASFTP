import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String username,password;
    private ReentrantLock lock;
    public User(String username,String password) {
        this.username = username;
        this.password = password;
        this.lock = new ReentrantLock();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public void lockUser() {
        this.lock.lock();
    }

    public void unlockUser() {
        this.lock.unlock();
    }

    public boolean isLocked() {
        return this.lock.isLocked();
    }
}
