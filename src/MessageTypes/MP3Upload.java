package MessageTypes;

import Models.Music;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MP3Upload  implements  Message{
    private int idUser;
    private MessageTypes type;
    private Music music;
    private String fileName;
    private boolean validMessage = true;
    public MP3Upload(int idUser, Music music) {
        this.type = MessageTypes.MP3Upload;
        this.idUser = idUser;
        this.music = music;
        this.fileName = music.getFilePath();
    }


    public MP3Upload(String request) {
        Pattern pattern = Pattern.compile("\\[(.*);(.*)](.*);(.*);(.*);(.*)\\[(.*)]"); // [4,0]teste.mp3;teste;criadorsoueu;1999[jazz;rock]
        Matcher match = pattern.matcher(request);
        if(match.matches()) {
            this.type = MessageTypes.fromInt(Integer.parseInt(match.group(1)));
            this.idUser = Integer.parseInt(match.group(2));
            this.fileName = match.group(3);
            String musicName = match.group(4);
            String artist = match.group(5);
            String yearMusic = match.group(6);
            String[] stringsTags = match.group(7).split(";");
            this.music = new Music(musicName,artist,yearMusic,this.fileName);
            for(String s : stringsTags)
            this.music.addTag(s);
        } else {
            validMessage = false;
        }
    }

    public Music getMusic() {
        return this.music;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");// [4,0]teste.mp3;teste;criadorsoueu;1999[jazz;rock]
        sb.append(type.getType());
        sb.append(";");
        sb.append(idUser);
        sb.append("]");
        sb.append(fileName);
        sb.append(";");
        sb.append(music.getTitle());
        sb.append(";");
        sb.append(music.getArtist());
        sb.append(";");
        sb.append(music.getYear());
        sb.append("[");
        for(String tag : music.getTags()){
            sb.append(tag);
            sb.append(";");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public MessageTypes getMessageType() {
        return type;
    }

    @Override
    public boolean isValidMessage() {
        return this.validMessage;
    }
}
