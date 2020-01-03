package Models;


import MessageTypes.Notification;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class User {
    private String username,password;
    private int id;
    private List<Music> myMusicList;
    private Queue<Notification> notificationList;
    public User(String username,String password) {
        this.username = username;
        this.password = password;
        this.id = -1;
        myMusicList = new ArrayList<>();
        notificationList = new LinkedList<>();
    }

    public void updateLoginID(int loginID) {
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

    public void addNotificationToQueue(Notification notification) {
        notificationList.add(notification);
    }

    public Notification pollNotificationFromQueue() {
       return notificationList.poll();
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public int getID() {
        return this.id;
    }

}
