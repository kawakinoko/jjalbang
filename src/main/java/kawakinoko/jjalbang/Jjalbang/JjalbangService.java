package kawakinoko.jjalbang.Jjalbang;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JjalbangService {

    @Value(value = "classpath:upload-dir")
    private Resource path;

    private final List<String> fileType = Arrays.asList("image/png", "image/jpeg", "image/gif");

    public boolean upload(MultipartHttpServletRequest multipartRequest) {
        Iterator<String> fileNamesItr = multipartRequest.getFileNames();
        while (fileNamesItr.hasNext()) {
            String fileName = fileNamesItr.next();
            MultipartFile file = multipartRequest.getFile(fileName);
            if (!isImageFile(file)) {
                return false;
            }
            try {
                String path = getDestination(fileName);
                log.info(path);
                file.transferTo(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public Resource download(String fileName) {
        Path filePath = Paths.get(path.getFilename()).toAbsolutePath().normalize().resolve(fileName).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<FileResource> imagesList(int page, int size) {
        File[] filesList = new File(path.getFilename()).listFiles();
        if (filesList == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(filesList)
                .skip(page * size)
                .limit((page+1) * size)
                .filter(File::isFile)
                .map(File::getName)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    public long imagesTotalSize() {
        File[] filesList = new File(path.getFilename()).listFiles();
        if (filesList == null) {
            return 0;
        }
        return Arrays.stream(filesList)
                .filter(File::isFile).count();
    }

    private FileResource convert(String fileName) {
        FileResource fp = new FileResource.FileResourceBuilder()
                .name(fileName)
                .viewUrl("/images/" + fileName)
                .downloadUrl("/api/download?fileName=" + fileName)
                .build();
        return fp;
    }

    private boolean isImageFile(MultipartFile file) {
        return fileType.contains(file.getContentType().toLowerCase());
    }

    private String getDestination(String fileName) throws IOException {
        String currentTimeString = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        File targetDir = new File(path.getFilename());
        boolean targetDirExists = targetDir.exists() || targetDir.mkdirs();
        if (!targetDirExists) {
            throw new IOException("Failed to create directory");
        }
        return targetDir.getAbsolutePath() + "/" + currentTimeString + "_" + fileName;
    }
}
