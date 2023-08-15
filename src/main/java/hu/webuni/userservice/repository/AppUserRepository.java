package hu.webuni.userservice.repository;


import hu.webuni.userservice.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findAppuserByUsername(String username);
    Optional<AppUser> findAppUserByFacebookId(String fbId);
}
