package MessageTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notification implements Message {
    private int idUser;
    private MessageTypes type;

    public Notification(int idUser) {
        this.type = MessageTypes.Notification;
        this.idUser = idUser;
    }

    public Notification(String request) {
        Pattern pattern = Pattern.compile("\\[(.*);(.*)]");
        Matcher match = pattern.matcher(request);
        if(match.matches()) {
            this.type = MessageTypes.fromInt(Integer.parseInt(match.group(1)));
            this.idUser = Integer.parseInt(match.group(2));

        } //TODO: throw exception
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
        return sb.toString();
    }
}
