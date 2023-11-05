//package hu.thesis.userservice.security;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.auth0.jwt.interfaces.Claim;
//import com.auth0.jwt.interfaces.DecodedJWT;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//import java.security.interfaces.ECPrivateKey;
//import java.security.interfaces.ECPublicKey;
//
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@RequiredArgsConstructor
//@Service
//public class JwtTokenService {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtTokenService.class);
//    private static final String AUTH = "auth";
//    private String issuer = "WebshopApp";
//    private Algorithm signerAlg;
//    private Algorithm validatorAlg;
//    private Algorithm alg = Algorithm.HMAC256("mysecret");
//
//    @Value("${hu.webuni.tokenlib.keypaths.private:#{null}}")
//    private String pathToPemWithPrivateKey;
//    @Value("${hu.webuni.tokenlib.keypaths.public:#{null}}")
//    private String pathToPemWithPublicKey;
//
//    @PostConstruct
//    public void init() throws Exception {
//        if(pathToPemWithPrivateKey != null) {
//            signerAlg = Algorithm.ECDSA512(null, (ECPrivateKey) PemUtils.getPrivateKey(pathToPemWithPrivateKey));
//        }
//
////        if(pathToPemWithPublicKey != null) {
////            validatorAlg = Algorithm.ECDSA512((ECPublicKey) PemUtils.getPublicKey(pathToPemWithPublicKey), null);
////        }
//    }
//
//    public String generateAccessToken(UserDetails userDetails) {
//        logger.info("Generate new access token for user: {}", userDetails.getUsername());
//
//        return JWT.create()
//                .withSubject(userDetails.getUsername())
//                .withArrayClaim(AUTH, userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
//                .withIssuedAt(new Date(System.currentTimeMillis()))
//                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(200)))
//                .withIssuer(issuer)
//                .withJWTId(generateUUID())
//                .sign(alg);
//    }
//
//    public UserDetails parseJwt(String jwtToken) {
//
//        DecodedJWT decodedJwt = JWT.require(alg)
//                .withIssuer(issuer)
//                .build()
//                .verify(jwtToken);
//        return new User(decodedJwt.getSubject(), "dummy",
//                decodedJwt.getClaim(AUTH).asList(String.class)
//                        .stream().map(SimpleGrantedAuthority::new).toList());
//
//    }
//
//    private String generateUUID() {
//        return UUID
//                .randomUUID()
//                .toString();
//    }
//
//    public DecodedJWT getAllClaimsFromToken(String token) {
//        return JWT.require(validatorAlg)
//                .withIssuer(issuer)
//                .build()
//                .verify(token);
//    }
//
//    public void validateToken(String token) {
//        logger.info("Getting all claims from token.");
//        Map<String, Claim> claims = getAllClaimsFromToken(token).getClaims();
//        List<String> errors = new ArrayList<>();
//
//        if (!claims.get("iss").asString().equals(issuer)) {
//            errors.add("Issuer is not matched");
//        }
//        if (!claims.get("exp").asDate().after((new Date()))) {
//            errors.add("Token is expired");
//        }
//        if (!errors.isEmpty()) {
//            throw new BadCredentialsException(
//                    "Error during validating token {%s}".formatted(errors.toString())
//            );
//        }
//    }
//
//}
