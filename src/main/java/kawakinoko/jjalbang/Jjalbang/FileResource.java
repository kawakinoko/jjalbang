package kawakinoko.jjalbang.Jjalbang;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResource {
    private String name;
    private String viewUrl;
    private String downloadUrl;
}
