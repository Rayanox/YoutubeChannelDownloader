package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.consumer.video;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
public class VideoDownloadProgress {

    public static int PROGRESS_CODE_ERROR = -2;
    public static int PROGRESS_CODE_NOT_FOUND = -1;

    private final HashMap<String, Integer> videoProgressionByReceipe = new HashMap<>();

    public Optional<Integer> getVideoProgressionForReceipe(String receipeName) {
        return Optional.ofNullable(videoProgressionByReceipe.get(receipeName));
    }

    public void updateDownloadProgression(String receipeName, int progressionPercent) {
        videoProgressionByReceipe.put(receipeName, progressionPercent);
    }

    public boolean isAlreadyDowloaded(String recetteName) {
        return videoProgressionByReceipe.getOrDefault(recetteName, -1) >= 100;
    }

    public boolean isAlreadyStarted(String recetteName) {
        return videoProgressionByReceipe.containsKey(recetteName);
    }
}
