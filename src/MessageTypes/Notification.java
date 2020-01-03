package MessageTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notification implements Message {
    private int userID;
    private MessageTypes type;
    private String notification;
    private boolean validMessage = true;
    private Notification(){
        this.type = MessageTypes.Notification;
    }
    public Notification(int user) {
        this();
        this.userID = user;
    }



    public Notification(String request) {
        this();
        if(request != null) {
            Pattern pattern = Pattern.compile("\\[(.*);(.*)]");
            Matcher match = pattern.matcher(request);
            if (match.matches()) {
                this.userID = Integer.parseInt(match.group(2));
            } //TODO: throw exception
        } else {
            validMessage = false;
        }
    }

    public int getUserID() {
        return userID;
    }

    public boolean isValidMessage() {
        return validMessage;
    }

    @Override
    public MessageTypes getMessageType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(type.getType());
        sb.append(";");
        sb.append(getUserID());
        sb.append("]");
        return sb.toString();
    }

}
