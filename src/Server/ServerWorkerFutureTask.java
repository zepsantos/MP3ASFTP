package Server;

import MessageTypes.Message;
import MessageTypes.MessageTypes;

import java.util.Comparator;
import java.util.concurrent.FutureTask;

public class ServerWorkerFutureTask extends FutureTask<ServerWorkerFutureTask> implements Comparable<ServerWorkerFutureTask> {
    private ServerWorker task;
    @Override
    public int compareTo(ServerWorkerFutureTask another) {
        return messagePriority(task.getMessageConnection().getMessage())-messagePriority(another.getTask().getMessageConnection().getMessage());
    }
    public ServerWorkerFutureTask(ServerWorker serverWorker) {
        super(serverWorker,null);
        task = serverWorker;
    }

    public ServerWorker getTask() {
        return this.task;
    }

    private int messagePriority(Message m) {
        MessageTypes tmp = m.getMessageType();
        switch(tmp) {
            case Login:
            case Register:
                return 1;
            case ResponseMessage:
                return 2;
            case MP3Upload:
                return 3;
            default:
                return 0;
        }
    }
}
