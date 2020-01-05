package MessageTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageAuthentication implements Message {
        private String user;
        private String password;
        private MessageTypes type;
        private boolean validMessage = true;
        public MessageAuthentication(MessageTypes type, String user, String password) {
            this.type = type;
            this.user = user;
            this.password = password;
        }

        public MessageAuthentication(String request) {
            Pattern  pattern = Pattern.compile("\\[(.*)](.*);(.*)");
            Matcher match = pattern.matcher(request);
            if(match.matches()) {
                this.type = MessageTypes.fromInt(Integer.parseInt(match.group(1)));
                this.user = match.group(2);
                this.password = match.group(3);
            } else {
                validMessage = false;
            }
        }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(type.getType());
        sb.append("]");
        sb.append(user);
        sb.append(";");
        sb.append(password);
        return sb.toString();
    }


    public MessageTypes getMessageType() {
        return type;
    }

    @Override
    public boolean isValidMessage() {
        return this.validMessage;
    }
}
