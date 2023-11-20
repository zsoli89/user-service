package hu.thesis.userservice.service;

import hu.thesis.userservice.model.dto.LoginDto;
import hu.thesis.userservice.model.entity.AppUser;
import hu.thesis.userservice.model.entity.ResponsibilityAppUser;
import hu.thesis.userservice.repository.AppUserRepository;
import hu.thesis.userservice.repository.ResponsibilityAppUserRepository;
import hu.thesis.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);
    private final AppUserRepository appUserRepository;
    private final ResponsibilityAppUserRepository responsibilityAppUserRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;

    public Map<String, String> login(UserDetails userDetails) {
        try {
            Map<String, String> tokenMap = new HashMap<>();
            LOGGER.info("Authentication set for user: {}", userDetails.getUsername());
            MDC.put("username", userDetails.getUsername());
            String accessToken = jwtTokenService.generateAccessToken(userDetails);
            tokenMap.put("accessToken", accessToken);
            return tokenMap;
        } catch (BadCredentialsException | ResponseStatusException e) {
            LOGGER.error("Error during login. message: {}", e.getMessage());
            throw e;
        }
    }

    public String logout(String username) {
        try {
            if (!MDC.get("username").equals(username))
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error during delete user from redis");
        }
        LOGGER.info("{} user successfully logged out.", username);
        return "LOGGED OUT";
    }

    public void registerUser(LoginDto loginDto) {
        if(loginDto == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "");
        Optional<AppUser> optionalAppUser = appUserRepository.findAppuserByUsername(loginDto.getUsername());
        if (optionalAppUser.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists.");
        AppUser appUser = appUserRepository.save(AppUser.builder()
                .username(loginDto.getUsername())
                .password(passwordEncoder.encode(loginDto.getPassword()))
                .build());
        responsibilityAppUserRepository.save(
                ResponsibilityAppUser.builder()
                        .username(appUser.getUsername())
                        .role("customer")
                        .build()
        );
    }

    public AppUser findById(Long id) {
        return appUserRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

}
