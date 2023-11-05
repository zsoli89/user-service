package hu.thesis.userservice.service;

import hu.thesis.userservice.model.entity.AppUser;
import hu.thesis.userservice.model.entity.ResponsibilityAppUser;
import hu.thesis.userservice.repository.AppUserRepository;
import hu.thesis.userservice.repository.ResponsibilityAppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class InitDbService {

    private final JdbcTemplate jdbcTemplate;
    private final AppUserRepository appUserRepository;
    private final ResponsibilityAppUserRepository responsibilityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void deleteDb() {
        appUserRepository.deleteAllInBatch();
        responsibilityRepository.deleteAllInBatch();
    }

    @Transactional
    public void deleteAudTables() {
        jdbcTemplate.update("DELETE FROM revinfo");
    }

    @Transactional
    public void addInitData() {
        AppUser user1 = createUser("jakab.zoltan", "ugyejo");
        AppUser user2 = createUser("gelesztas.gazsi", "dummy");
        createRole(user1.getUsername(), "admin");
        createRole(user1.getUsername(), "customer");
        createRole(user2.getUsername(), "customer");
    }

    @Transactional
    public AppUser createUser(String username, String password) {
        return appUserRepository.save(
                AppUser.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .build()
        );
    }

    @Transactional
    public void createRole(String username, String role) {
        responsibilityRepository.save(
                ResponsibilityAppUser.builder()
                        .username(username)
                        .role(role)
                        .build()
        );
    }

}
