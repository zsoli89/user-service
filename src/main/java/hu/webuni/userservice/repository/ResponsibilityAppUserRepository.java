package hu.webuni.userservice.repository;


import hu.webuni.userservice.model.entity.ResponsibilityAppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponsibilityAppUserRepository extends JpaRepository<ResponsibilityAppUser, Long> {

    List<ResponsibilityAppUser> findResponsibilityAppUserByUsername(String username);
}
