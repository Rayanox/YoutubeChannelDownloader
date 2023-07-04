package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.consumer.video;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.quality.VideoQuality;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import rb.nutritiongoodfit.backend.wsbackendnutrition.infra.file.VideoFileService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
public class YoutubeConsumer extends VideoConsumer {

    private static final VideoQuality EXPECTED_QUALITY = VideoQuality.medium; //medium = 360p

    private final VideoFileService videoFileService;

    public YoutubeConsumer(VideoDownloadProgress downloadProgress, VideoFileService videoFileService) {
        super(downloadProgress);
        this.videoFileService = videoFileService;
    }

    public VideoInfo getVideoInfo(String url, YoutubeDownloader downloader) {
        String videoId = getVideoId(url);
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = downloader.getVideoInfo(request);
        VideoInfo video = response.data();
        return video;
    }

    public VideoFormat getVideoFormat(String url, YoutubeDownloader downloader) {
        VideoInfo videoInfo = getVideoInfo(url, downloader);
        return getVideoFormat(videoInfo);
    }

    public VideoFormat getVideoFormat(VideoInfo videoInfo) {
        return videoInfo.videoFormats().stream()
                .sorted(Comparator.comparing(videoFormat -> videoFormat.videoQuality(), (v1, v2) -> v1.compare(v2)))
                .filter(videoFormat -> videoFormat.videoQuality().compare(EXPECTED_QUALITY) >= 0)
                .findFirst()
                .orElseThrow();
    }

    public void downloadVideo(String receipeName, VideoFormat format, YoutubeDownloader downloader) {
        RequestVideoFileDownload request = new RequestVideoFileDownload(format)
                .saveTo(VideoFileService.VIDEO_DIRECTORY_FILE)
                .renameTo(receipeName)
                .callback(new YoutubeProgressCallback<File>() {
                    @Override
                    public void onDownloading(int progress) {
                        downloadProgress.updateDownloadProgression(receipeName, progress);
                        System.out.printf("Downloaded %d%%\n", progress);
                    }

                    @Override
                    public void onFinished(File videoInfo) {
                        System.out.println("Finished file: " + videoInfo);
//                        try {
//                            byte [] bytes = Files.readAllBytes(videoInfo.toPath());
//                            videoFileService.writeVideoFile(receipeName, bytes);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            log.error("Fail of file writing of downloaded video of receipe " + receipeName,e);
//                            downloadProgress.updateDownloadProgression(receipeName, VideoDownloadProgress.PROGRESS_CODE_ERROR);
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Error: " + e.getLocalizedMessage());
                        log.error("Download of receipe "+receipeName+" failed",e);
                    }
                })
                .async();

        Response<File> response = downloader.downloadVideoFile(request);
    }

    private String getVideoId(String url) {
        Matcher matcher = Pattern.compile("watch\\?v=(.*)").matcher(url);
        if(!matcher.find()) {
            String errorMessage = "Cannot parse videoId from URL -> " + url;
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        return matcher.group(1);
    }

    @Override
    boolean isValidConsumerForUrl(String url) {
        return url.contains("youtube.com") || url.contains("youtu.be");
    }
}
