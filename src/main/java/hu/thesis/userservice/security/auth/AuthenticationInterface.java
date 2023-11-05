package hu.thesis.userservice.security.auth;

import org.springframework.security.core.Authentication;

import java.security.GeneralSecurityException;

public interface AuthenticationInterface {

    Authentication authenticate(String username, String password)
            throws GeneralSecurityException, InterruptedException;
}
