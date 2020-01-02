package MessageTypes;

import MessageTypes.MessageTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseMessage implements Message {
    private int userID;
    private String response;
    private MessageTypes type;
    private boolean validMessage = true;
    public ResponseMessage(MessageTypes type,int user,String response) {
        this.type = type;
        this.userID = user;
        this.response = response;
    }

    public ResponseMessage(String request) {
        if(request != null) {
            Pattern pattern = Pattern.compile("\\[(.*);(.*)](.*)");
            Matcher match = pattern.matcher(request);
            if (match.matches()) {
                this.type = MessageTypes.fromInt(Integer.parseInt(match.group(1)));
                this.userID = Integer.parseInt(match.group(2));
                this.response = match.group(3);
            } else {
                validMessage = false;//TODO: throw exception
            }
        } else {
            validMessage = false;
        }
    }

    public int getUserID() {
        return userID;
    }

    public String getResponse() {
        return response;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(type.getType());
        sb.append(";");
        sb.append(userID);
        sb.append("]");
        sb.append(response);
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
