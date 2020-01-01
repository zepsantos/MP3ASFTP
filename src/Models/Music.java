package Models;

import java.util.ArrayList;

public class Music {
    private String title,artist,year;
    private ArrayList<String> tags;
    private int musicID;

    public Music(String title, String artist, String year ,int musicID) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.musicID = musicID;
        this.tags = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ArrayList<String> getTags() {
        return new ArrayList<>(tags);
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public int getMusicID() {
        return musicID;
    }

    public void setMusicID(int musicID) {
        this.musicID = musicID;
    }
}
