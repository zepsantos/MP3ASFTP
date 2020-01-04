package Models;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Music {
    private String title,artist,year,filePath;
    private ArrayList<String> tags;
    private int musicID;
    private AtomicInteger nTimesMusicHasBeenDownloaded;
    private int ownerOfUploadID;

    public Music(String title, String artist, String year,String filePath) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.filePath = filePath;
        this.tags = new ArrayList<>();
        this.nTimesMusicHasBeenDownloaded = new AtomicInteger();
    }

    public Music (String title, String artist,String year , int musicID,int nTimesMusicHasBeenDownloaded, int ownerOfUploadID ,ArrayList<String> tagsList, String filePath) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.nTimesMusicHasBeenDownloaded = new AtomicInteger(nTimesMusicHasBeenDownloaded);
        this.musicID = musicID;
        this.filePath = filePath;
        this.ownerOfUploadID = ownerOfUploadID;
        this.tags = new ArrayList<>(tagsList);
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

    public int getnTimesMusicHasBeenDownloaded() {
        return nTimesMusicHasBeenDownloaded.get();
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

    public void incrementMusicDownloaderCounter() {this.nTimesMusicHasBeenDownloaded.getAndIncrement();}

    public void setOwnerOfUploadID(int ownerOfUploadID) {
        this.ownerOfUploadID = ownerOfUploadID;
    }

    public int getOwnerOfUploadID() {
        return ownerOfUploadID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Object clone() {
        return new Music(getTitle(),getArtist(),getYear(),getMusicID(),this.nTimesMusicHasBeenDownloaded.get(),this.ownerOfUploadID,this.tags,getFilePath());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        sb.append(title).append(';');
        sb.append(artist).append(';');
        sb.append(year).append(';');
        sb.append(filePath).append(';');
        for(String s : this.tags)
            sb.append(s).append(',');
        sb.append(';');
        sb.append(musicID).append(';');
        sb.append(nTimesMusicHasBeenDownloaded.get()).append(';');
        sb.append(ownerOfUploadID).append(';');
        return sb.toString();
    }
}
