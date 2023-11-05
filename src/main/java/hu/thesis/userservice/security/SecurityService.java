package hu.thesis.userservice.security;

import hu.thesis.userservice.dto.LoginDto;
import hu.thesis.userservice.security.entity.AppUser;
import hu.thesis.userservice.security.entity.ResponsibilityAppUser;
import hu.thesis.userservice.security.repository.AppUserRepository;
import hu.thesis.userservice.security.repository.ResponsibilityAppUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    private final AppUserRepository appUserRepository;
    private final ResponsibilityAppUserRepository responsibilityAppUserRepository;
    private static final String BEARER = "Bearer ";
    private final JwtTokenService jwtTokenService;

    private final PasswordEncoder passwordEncoder;

    public Map<String, String> login(UserDetails userDetails) {
        try {
            Map<String, String> tokenMap = new HashMap<>();
            logger.info("Authentication set for user: {}", userDetails.getUsername());
            MDC.put("username", userDetails.getUsername());
            String accessToken = jwtTokenService.generateAccessToken(userDetails);
            tokenMap.put("accessToken", accessToken);
            return tokenMap;
        } catch (BadCredentialsException | ResponseStatusException e) {
            logger.error("Error during login. message: {}", e.getMessage());
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
        logger.info("{} user successfully logged out.", username);
        return "LOGGED OUT";
    }

    public UsernamePasswordAuthenticationToken validateAccessToken(String token) {
        token = tokenBearerRemover(token);
        if (token == null)
            return null;
        UserDetails principal = jwtTokenService.parseJwt(token);
        jwtTokenService.validateToken(token);

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    private String tokenBearerRemover(String token) {
        logger.info("Token bearer remover on: {}", token);
        if (token != null && token.startsWith(BEARER)) {
            return token.substring(BEARER.length());
        } else {
            return null;
        }
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
        appUserRepository.save(appUser);
    }
}
