package hu.webuni.userservice.security.auth;

import hu.webuni.security.entity.AppUser;
import hu.webuni.security.repository.AppUserRepository;
import hu.webuni.userservice.security.WebshopUserDetailsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FacebookAuthService {

    private static final String GRAPH_API_BASE_URL = "https://graph.facebook.com/v13.0";

    private final AppUserRepository appUserRepository;
    private final WebshopUserDetailsService webshopUserDetailsService;

    @Getter
    @Setter
    public static class FacebookData {
        private String email;
        private long id;
    }


    @Transactional
    public UserDetails getUserDetailsForToken(String fbToken) {
        FacebookData fbData = getEmailOfFbUser(fbToken);
        AppUser appUser = findOrCreateUser(fbData);
        return webshopUserDetailsService.createUserDetails(appUser);

    }

    private FacebookData getEmailOfFbUser(String fbToken) {
        return WebClient
                .create(GRAPH_API_BASE_URL)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me")
                        .queryParam("fields", "email,name")
                        .build()
                )
                .headers(headers -> headers.setBearerAuth(fbToken))
                .retrieve()
                .bodyToMono(FacebookData.class)
                .block();
    }


    private AppUser findOrCreateUser(FacebookData facebookData) {
        String fbId = String.valueOf(facebookData.getId());
        Optional<AppUser> optionalExistingUser = appUserRepository.findAppUserByFacebookId(fbId);
        if(optionalExistingUser.isEmpty()) {
            AppUser newUser = AppUser.builder()
                    .facebookId(fbId)
                    .username(facebookData.getEmail())
                    .password("dummy")
                    .build();
            return appUserRepository.save(newUser);
        } else {
            return optionalExistingUser.get();
        }
    }

}
