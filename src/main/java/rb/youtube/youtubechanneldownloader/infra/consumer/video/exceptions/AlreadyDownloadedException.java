package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.consumer.video.exceptions;

public class AlreadyDownloadedException extends Exception {

    public AlreadyDownloadedException(String receipeName) {
        super("Download already done for receipe '" + receipeName + "'");
    }
}
