package hu.thesis.userservice.controller;

import hu.thesis.userservice.model.dto.LoginDto;
import hu.thesis.userservice.model.entity.AppUser;
import hu.thesis.userservice.security.auth.UsernamePassAuthService;

import hu.thesis.userservice.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/security")
public class SecurityController {

    private final SecurityService securityService;
    private final UsernamePassAuthService authService;

    @PostMapping("/free/login")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> login(@RequestBody LoginDto loginDto) {
        UserDetails userDetails;
        Authentication authentication = authService.authenticate(loginDto.getUsername(), loginDto.getPassword());
        userDetails = (UserDetails) authentication.getPrincipal();
        return securityService.login(userDetails);
    }

    @PostMapping("/free/register")
    @ResponseStatus(HttpStatus.OK)
    public void registerUser(@RequestBody LoginDto loginDto) {
        securityService.registerUser(loginDto);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/find/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AppUser findById(@PathVariable Long id) {
        return securityService.findById(id);
    }

    @GetMapping("/logout/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String logoutByUsername(@PathVariable String username) {
        return securityService.logout(username);
    }

    @GetMapping("/findAll")
    @PreAuthorize("hasAuthority('admin')")
    @ResponseStatus(HttpStatus.OK)
    public List<AppUser> findAll() {
        return securityService.findAll();
    }
}
