package hu.thesis.userservice;

import hu.thesis.userservice.service.InitDbService;
import hu.webuni.security.JwtAuthFilter;
import hu.webuni.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {UserServiceApplication.class, JwtAuthFilter.class, JwtTokenService.class})
@RequiredArgsConstructor
public class UserServiceApplication implements CommandLineRunner {

	private final InitDbService initDbService;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		initDbService.deleteDb();
		initDbService.addInitData();
	}
}
