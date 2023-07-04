package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.consumer.video;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.quality.VideoQuality;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import rb.nutritiongoodfit.backend.wsbackendnutrition.infra.file.VideoFileService;

import java.io.File;
import java.io.InputStream;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class VideoDownloadService {

    private final YoutubeConsumer youtubeConsumer;

    public void downloadVideo(String recetteName, String urlVideo) {
        if(!youtubeConsumer.isValidConsumerForUrl(urlVideo)) {
            throw new RuntimeException("The URL is not compatible with the Youtube consumer. Please create a new consumer for this URL or change the URL to use a Youtube one.");
        }

        YoutubeDownloader downloader = new YoutubeDownloader();
        VideoFormat videoFormat = youtubeConsumer.getVideoFormat(urlVideo, downloader);
        youtubeConsumer.downloadVideo(recetteName, videoFormat, downloader); //VideoFormat contient l'URL
    }

}
