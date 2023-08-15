package hu.webuni.userservice.security;

import hu.webuni.userservice.dto.LoginDto;
import hu.webuni.userservice.security.entity.AppUser;
import hu.webuni.userservice.security.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
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
    private static final String BEARER = "Bearer ";
    private final JwtTokenService jwtTokenService;
    private final RedisService redisService;
    @Value("${redis.user.postfix}")
    private String redisUserPostfix;
    @Value("${redis.user.refresh.postfix}")
    private String redisUserRefreshPostfix;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, String> login(UserDetails userDetails) {
        try {
            Map<String, String> tokenMap = new HashMap<>();
            String username = userDetails.getUsername();
            logger.info("Authentication set for user: {}", userDetails.getUsername());
            MDC.put("username", userDetails.getUsername());
            String id = redisService.getValueFromRedis(username + redisUserPostfix);
            if (id == null || id.isEmpty()) {
                String accessToken = jwtTokenService.generateAccessToken(userDetails);
                String refreshToken = jwtTokenService.generateRefreshToken(userDetails);
                tokenMap.put("accessToken", accessToken);
                tokenMap.put("refreshToken", refreshToken);
                return tokenMap;
            } else {
                logger.error("Duplicate login! Username: {}", username);
                throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT);
            }
        } catch (BadCredentialsException | ResponseStatusException e) {
            logger.error("Error during login. message: {}", e.getMessage());
            throw e;
        }
    }

    public String logout(String username) {
        try {
            if (!MDC.get("username").equals(username))
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            redisService.deleteFromRedis(username + redisUserPostfix);
            redisService.deleteFromRedis(username + redisUserRefreshPostfix);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Error during delete user from redis");
        }
        logger.info("{} user successfully logged out.", username);
        return "LOGGED OUT";
    }

    public UsernamePasswordAuthenticationToken validateAccessToken(String token) {
        token = tokenBearerRemover(token);
        if (token == null)
            return null;
        UserDetails principal = jwtTokenService.parseJwt(token);
        jwtTokenService.validateToken(token, principal, redisUserPostfix);

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    private String tokenBearerRemover(String token) {
        logger.info("Token bearer remover on: {}",token);
        if (token != null && token.startsWith(BEARER)) {
            return token.substring(BEARER.length());
        } else {
            return null;
        }
    }

    public void registerUser(LoginDto loginDto) {
        Optional<AppUser> optionalAppUser = appUserRepository.findAppuserByUsername(loginDto.getUsername());
        if (optionalAppUser.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists.");
        AppUser appUser = AppUser.builder()
                .username(loginDto.getUsername())
                .password(passwordEncoder.encode(loginDto.getPassword()))
                .build();
        appUserRepository.save(appUser);
    }
}
