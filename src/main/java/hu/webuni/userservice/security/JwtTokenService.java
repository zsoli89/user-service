package hu.webuni.userservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final RedisService redisService;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
    private static final String AUTH = "auth";
    private Algorithm alg = Algorithm.HMAC256("mysecret");
    private static final String BEARER = "Bearer ";
    private String issuer = "WebshopApp";
    @Value("${redis.user.postfix}")
    private String redisUserPostfix;
    @Value("${redis.user.refresh.postfix}")
    private String redisUserRefreshPostfix;
    @Value("${jwt.secret}")
    private String secret;

    public String generateAccessToken(UserDetails userDetails) {
        logger.info("Generate new access token for user: {}", userDetails.getUsername());
        String id = saveTokenToRedis(userDetails.getUsername(), redisUserPostfix, 600L);

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withArrayClaim(AUTH, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20)))
                .withIssuer(issuer)
                .withJWTId(id)
                .sign(alg);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        logger.info("Generate new refresh token for user: {}", userDetails.getUsername());
        String id = saveTokenToRedis(userDetails.getUsername(), redisUserRefreshPostfix, 1800L);

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withArrayClaim(AUTH, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(200)))
                .withIssuer(issuer)
                .withJWTId(id)
                .sign(alg);
    }

    public UserDetails parseJwt(String jwtToken) {

        DecodedJWT decodedJwt = JWT.require(alg)
                .withIssuer(issuer)
                .build()
                .verify(jwtToken);
        return new User(decodedJwt.getSubject(), "dummy",
                decodedJwt.getClaim(AUTH).asList(String.class)
                        .stream().map(SimpleGrantedAuthority::new).toList());

    }

    private String generateUUID() {
        return UUID
                .randomUUID()
                .toString();
    }

    private String saveTokenToRedis(String username, String keyExt, Long expire) {
        String id = generateUUID();
        String redisKey = username + keyExt;
        redisService.setValueWithExpiration(redisKey, id, expire);
        logger.info("Token stored in Redis, response");
        return id;
    }

    public DecodedJWT getAllClaimsFromToken(String token) {
        return JWT.require(alg)
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

    public void validateToken(String token, UserDetails userDetails, String keySuffix) {
        logger.info("Getting all claims from token.");
        final String username = userDetails.getUsername();
        Map<String, Claim> claims = getAllClaimsFromToken(token).getClaims();
        String redisKey = username + keySuffix;
        String redisResponse = redisService.getValueFromRedis(redisKey);
        List<String> errors = new ArrayList<>();

        if ((redisResponse == null) || (redisResponse.isEmpty())) {
            errors.add("Token doesn't exist in REDIS");
        }
        if (!claims.get("iss").asString().equals(issuer)) {
            errors.add("Issuer is not matched");
        }
        if (!claims.get("jti").asString().equals(redisResponse)) {
            errors.add("Token is not matched");
        }
        if (!claims.get("exp").asDate().after((new Date()))) {
            errors.add("Token is expired");
        }
        if (!errors.isEmpty()) {
            throw new BadCredentialsException(
                    "Error during validating token {%s}".formatted(errors.toString())
            );
        }
    }

}
