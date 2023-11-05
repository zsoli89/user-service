package hu.thesis.userservice.security.repository;


import hu.thesis.userservice.security.entity.ResponsibilityAppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponsibilityAppUserRepository extends JpaRepository<ResponsibilityAppUser, Long> {

    List<ResponsibilityAppUser> findResponsibilityAppUserByUsername(String username);
}
