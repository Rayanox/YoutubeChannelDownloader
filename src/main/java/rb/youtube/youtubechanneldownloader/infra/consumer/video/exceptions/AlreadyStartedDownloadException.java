package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.consumer.video.exceptions;

public class AlreadyStartedDownloadException extends Exception{

    public AlreadyStartedDownloadException(String receipeName, int progression) {
        super("Download already started for receipe '" + receipeName + "' (" + progression +" %)");
    }
}
