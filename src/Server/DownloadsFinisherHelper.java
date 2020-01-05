package Server;

class DownloadsFinisherHelper {
    private static DownloadsFinisherHelper inst = null;
    private DownloadFinishedListener downloadFinishedListener;

    private DownloadsFinisherHelper() {
    }

    static DownloadsFinisherHelper getInstance() {
        if (inst == null) {
            inst = new DownloadsFinisherHelper();
        }
        return inst;
    }

    DownloadFinishedListener getDownloadFinishedListener() {
        return downloadFinishedListener;
    }

    void setDownloadFinishedListener(DownloadFinishedListener downloadFinishedListener) {
        this.downloadFinishedListener = downloadFinishedListener;
    }
}
