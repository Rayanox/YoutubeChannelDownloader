package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.file;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Log4j2
@Service
public class VideoFileService {

    private static final String RECETTENAME_KEY = "${RECETTE_NAME}";
    private static final String EXTENSION_KEY = "${EXTENSION}";

    private static final String VIDEO_FOLDER = "videos";
    public static final File VIDEO_DIRECTORY_FILE = new File(VIDEO_FOLDER);

    public Optional<VideoFileDto> getVideoFile(String recetteName) {
        return getFileByRecetteName(recetteName)
                .map(videoFile -> mapVideoDto(recetteName, videoFile));
    }


    public String getMediaType(VideoFileDto videoFileDto) {
        return MediaType.parseMediaType("video/" + videoFileDto.getExtension()).toString();
    }

    /*
        PRIVATES
     */

    private VideoFileDto mapVideoDto(String recetteName, File file) {
        String fileName = file.getName();
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(Path.of(file.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return VideoFileDto.builder()
                .recetteName(recetteName)
                .fileName(fileName)
                .extension(getExtension(fileName))
                .inputStream(inputStream)
                .build();
    }

    private Optional<File> getFileByRecetteName(String recetteName) {
        try {
            List<String> paths = Files.list(Path.of(VIDEO_FOLDER))
                    .filter(path -> filterRecetteName(recetteName, path.getFileName().toString()))
                    .map(path -> path.toAbsolutePath().toString())
                    .collect(Collectors.toList());

            if(paths.size() > 1) {
                log.warn("More than 1 video file is present in video folder for receipe name '{}'", recetteName);
            }
            if(paths.isEmpty()) {
                log.error("No video file exists in video folder for receipe name '{}'", recetteName);
                return Optional.empty();
            }
            return Optional.of(new File(paths.get(0)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean filterRecetteName(String recetteName, String fileName) {
        return fileName.contains(inverseReplaceSpecialCharacters(recetteName));
    }

    private CharSequence inverseReplaceSpecialCharacters(String recetteName) {
        return recetteName.replace(':', '_');
    }
}
