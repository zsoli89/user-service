package hu.webuni.userservice.controller;

import hu.webuni.userservice.dto.LoginDto;
import hu.webuni.userservice.security.SecurityService;
import hu.webuni.userservice.security.auth.FacebookAuthService;
import hu.webuni.userservice.security.auth.UsernamePassAuthService;
import io.netty.util.internal.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user-service/security")
public class SecurityController {

    private final SecurityService securityService;
    private final UsernamePassAuthService authService;
    private final FacebookAuthService facebookAuthService;

    @PostMapping("/free/login")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> login(@RequestBody LoginDto loginDto) {
        UserDetails userDetails = null;
        String fbToken = loginDto.getFbToken();
        if (ObjectUtils.isEmpty(fbToken)) {
            Authentication authentication = authService.authenticate(loginDto.getUsername(), loginDto.getPassword());
            userDetails = (UserDetails) authentication.getPrincipal();
            return securityService.login(userDetails);
        } else {
            userDetails = facebookAuthService.getUserDetailsForToken(fbToken);
            return securityService.login(userDetails);
        }
    }

    @PostMapping("/free/register")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@RequestBody LoginDto loginDto) {
        securityService.registerUser(loginDto);
    }

    @GetMapping("/logout/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String logoutByUsername(@PathVariable String username) {
        return securityService.logout(username);
    }
}
