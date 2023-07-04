package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.consumer.video;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class VideoConsumer {

    protected final VideoDownloadProgress downloadProgress;

    abstract boolean isValidConsumerForUrl(String url);

}
