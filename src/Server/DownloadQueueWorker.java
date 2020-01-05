package Server;

import MessageTypes.MP3Download;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class DownloadQueueWorker implements Runnable {
    private Queue<MessageConnection> mp3DownloadQueue;
    private AtomicInteger downloadsAtTheSameTime;
    private HashMap<Integer, Integer> nDownloadsPerID;
    private ThreadPoolExecutor threadPoolExecutor;
    private final static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public DownloadQueueWorker(Queue<MessageConnection> queue , AtomicInteger counter , ThreadPoolExecutor executor, HashMap<Integer,Integer> nDownloadsPerId) {
        this.mp3DownloadQueue = queue;
        this.downloadsAtTheSameTime = counter;
        this.threadPoolExecutor = executor;
        this.nDownloadsPerID = nDownloadsPerId;
    }

    @Override
    public void run() {
        while(true) {
            while (!this.mp3DownloadQueue.isEmpty() && this.downloadsAtTheSameTime.get() < Server.MAX_DOWNLOAD_SAMETIME) {
                MessageConnection mp3mc = this.mp3DownloadQueue.poll();
                runMP3DownloadWorker(mp3mc);
                MP3Download mp3tmp = (MP3Download) mp3mc.getMessage();
                int tmpcounter = nDownloadsPerID.get(mp3tmp.getIdUser());
                this.nDownloadsPerID.put(mp3tmp.getIdUser(), --tmpcounter);
                log.info("MP3Download removido da queue e pronto a ser transferido");
            }
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e) {

            }
        }
    }

    private void runMP3DownloadWorker(MessageConnection messageConnection) {
        threadPoolExecutor.execute(new ServerWorkerFutureTask(new ServerWorker(messageConnection)));
        this.downloadsAtTheSameTime.incrementAndGet();
    }
}
