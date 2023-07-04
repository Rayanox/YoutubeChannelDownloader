package rb.nutritiongoodfit.backend.wsbackendnutrition.infra.file;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Builder
@Getter
public class VideoFileDto {

    private String recetteName;
    private String fileName;
    private InputStream inputStream;
    private String extension;

}
