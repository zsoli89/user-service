package hu.webuni.userservice.security.auth;

import hu.webuni.userservice.security.WebshopUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsernamePassAuthService implements AuthenticationInterface {

    private final PasswordEncoder passwordEncoder;
    private final WebshopUserDetailsService webshopUserDetailsService;

    public boolean isMatchesPassword(String incomingPassword, String encodedPassword) {
        return passwordEncoder.matches(incomingPassword, encodedPassword);
    }

    @Override
    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = webshopUserDetailsService.loadUserByUsername(username);

        if (isMatchesPassword(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        }
        else
            throw new BadCredentialsException("Invalid username or password");
    }

}
