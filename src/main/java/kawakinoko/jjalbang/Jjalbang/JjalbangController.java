package kawakinoko.jjalbang.Jjalbang;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/api")
public class JjalbangController {

    @Autowired
    JjalbangService jjalbangService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadJjalbang(MultipartHttpServletRequest multipartHttpServletRequest) {
        if (jjalbangService.upload(multipartHttpServletRequest)) {
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/download")
    public ResponseEntity<Resource> downloadJjalbang(@RequestParam String fileName) {
        Resource fileResource = jjalbangService.download(fileName);
        if (fileResource != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                    .body(fileResource);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/list")
    public ResponseEntity<PagedResources<FileResource>> listJjalbang(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                     @RequestParam(required = false, defaultValue = "20") Integer size) {
        return ResponseEntity.ok()
                .body(new PagedResources<>(jjalbangService.imagesList(page, size),
                        new PagedResources.PageMetadata(size, page, jjalbangService.imagesTotalSize())));
    }
}
