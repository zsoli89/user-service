package hu.webuni.userservice.security;

import hu.webuni.security.entity.AppUser;
import hu.webuni.security.entity.ResponsibilityAppUser;
import hu.webuni.security.repository.AppUserRepository;
import hu.webuni.security.repository.ResponsibilityAppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebshopUserDetailsService implements UserDetailsService {


    private final AppUserRepository userRepository;

    private final ResponsibilityAppUserRepository responsibilityRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser webshopUser = userRepository.findAppuserByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(username));
        return createUserDetails(webshopUser);
    }

    public List<String> getRoles(String username) {
        List<ResponsibilityAppUser> responsibilityAppUserList = responsibilityRepository.findResponsibilityAppUserByUsername(username);
        List<String> roles = new ArrayList<>();
        for(ResponsibilityAppUser item : responsibilityAppUserList)
            roles.add(item.getRole());
        return roles;
    }

    public UserDetails createUserDetails(AppUser appUser) {
        List<SimpleGrantedAuthority> roles = getRoles(appUser.getUsername())
                .stream()
                .map(SimpleGrantedAuthority::new).toList();

        return new User(appUser.getUsername(), appUser.getPassword(), roles);
    }

}
