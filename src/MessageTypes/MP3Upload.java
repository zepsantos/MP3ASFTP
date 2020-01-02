package MessageTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MP3Upload  implements  Message{
    private int idUser;
    private MessageTypes type;
    private String fileName;
    private boolean validMessage = true;
    public MP3Upload(int idUser,String fileName) {
        this.type = MessageTypes.MP3Upload;
        this.idUser = idUser;
        this.fileName = fileName;
    }

    public MP3Upload(String request) {
        Pattern pattern = Pattern.compile("\\[(.*);(.*)](.*)");
        Matcher match = pattern.matcher(request);
        if(match.matches()) {
            this.type = MessageTypes.fromInt(Integer.parseInt(match.group(1)));
            this.idUser = Integer.parseInt(match.group(2));
            this.fileName = match.group(3);
        } else {
            validMessage = false;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public int getIdUser() {
        return idUser;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(type.getType());
        sb.append(";");
        sb.append(idUser);
        sb.append("]");
        sb.append(fileName);
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
