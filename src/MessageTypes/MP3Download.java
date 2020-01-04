package MessageTypes;

import Models.Music;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MP3Download implements Message {
    private int idUser,idMusic;
    private MessageTypes type  = MessageTypes.MP3Download;
    private boolean validMessage = true;
    public MP3Download(int idUser, int idMusic) {
        this.idUser = idUser;
        this.idMusic = idMusic;
    }



    public MP3Download(String request) {
        Pattern pattern = Pattern.compile("\\[(.*);(.*)](.*)");
        Matcher match = pattern.matcher(request);
        if(match.matches()) {
            this.idUser = Integer.parseInt(match.group(2));
            this.idMusic = Integer.parseInt(match.group(3));
        } else {
            validMessage = false;
        }
    }



    public int getIdUser() {
        return idUser;
    }

    public int getIdMusic() {
        return idMusic;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(type.getType());
        sb.append(";");
        sb.append(idUser);
        sb.append("]");
        sb.append(idMusic);
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
