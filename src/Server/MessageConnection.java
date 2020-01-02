package Server;

import MessageTypes.Message;

import java.io.IOException;
import java.net.Socket;

public class MessageConnection {
    private Message message;
    private Socket socket;

    public MessageConnection(Message m,Socket s) {
        this.message = m;
        this.socket = s;
    }
    public Message getMessage() {
        return message;
    }

    public Socket getSocket() {
        return socket;
    }

    public void closeSocket() throws IOException {
        this.socket.close();
    }
}
