package kawakinoko.jjalbang.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/userinfo")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping
    ResponseEntity<Map> getUserInfo(Model model, OAuth2AuthenticationToken authentication) {
        Map userInfo = this.userInfoService.getLoginInfo(authentication);
        if (userInfo == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(userInfo);
    }
}
