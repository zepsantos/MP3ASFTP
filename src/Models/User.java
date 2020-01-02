package Models;


import java.util.ArrayList;
import java.util.List;

public class User {
    private String username,password;
    private int id;
    private List<Music> myMusicList;
    public User(String username,String password) {
        this.username = username;
        this.password = password;
        this.id = -1;
        myMusicList = new ArrayList<>();
    }

    public void updateInt(int loginID) {
        this.id = loginID;
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
    public int getID() {
        return this.id;
    }

}
