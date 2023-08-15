package hu.webuni.userservice.security.repository;


import hu.webuni.userservice.security.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findAppuserByUsername(String username);
    Optional<AppUser> findAppUserByFacebookId(String fbId);
}
