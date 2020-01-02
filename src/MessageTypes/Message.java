package MessageTypes;

public interface Message {
    static MessageTypes getMessageType(String message) {
        String header = message.substring(1,message.indexOf("]"));
        String messageType = null;
        if(header.contains(";")) {
            messageType = header.substring(0,header.indexOf(";"));
        } else {
            messageType = header;
        }
        return MessageTypes.fromInt(Integer.parseInt(messageType));

    }

    MessageTypes getMessageType();
    boolean isValidMessage();
}
