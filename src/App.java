import java.util.HashMap;

public class App {
    private HashMap<String,User> users;
    public App() {
        users = new HashMap<>();
    }

    public synchronized boolean registerUser(String username,String password) {
        if(!this.users.containsKey(username)) {
            User user = new User(username,password);
            users.put(username,user);
            return true;
        }
        return false;

    }

    public synchronized User loginUser(String username,String password) { //TODO: Ter cuidado com os locks
        if(this.users.containsKey(username)) {
            User user = this.users.get(username);
            if (user.getPassword().equals(password) && !user.isLocked()) {
                user.lockUser();
                return user;
            }
        }
        return null;
    }

    public synchronized void logout(String username) { //TODO: Ter cuidado com os locks
        if(this.users.containsKey(username)) {
            this.users.get(username).unlockUser();
        }

    }



}
