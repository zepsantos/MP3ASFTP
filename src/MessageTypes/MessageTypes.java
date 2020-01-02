package MessageTypes;

public enum MessageTypes {

    Login (1) , Register(2), ResponseMessage(3) ,MP3Upload(4),Notification(5);
    int type;

    MessageTypes(int type) {
        this.type = type;
    }

    public static MessageTypes fromInt(int type){
        for(MessageTypes t : MessageTypes.values()) {
            if(t.getType() == type) return t;
        }
        return null;
    }

    public Class<? extends Message>  getClassFromType() {
        switch(getType()) {
            case 1:
            case 2:
                return MessageAuthentication.class;
            case 3:
                return ResponseMessage.class;
            case 4:
                return MP3Upload.class;
            case 5:
                return Notification.class;
                default:
                break;

        }
        return null;
    }

    public int getType() {
        return type;
    }
}
