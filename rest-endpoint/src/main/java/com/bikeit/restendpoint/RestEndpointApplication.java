package com.bikeit.restendpoint;

import com.bikeit.restendpoint.model.Dto.RegistrationDto;
import com.bikeit.restendpoint.service.RoleService;
import com.bikeit.restendpoint.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.bikeit.restendpoint.model.Role;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories("com.bikeit.restendpoint.*")
@ComponentScan(basePackages = { "com.bikeit.restendpoint.*" })
@EntityScan("com.bikeit.restendpoint.*")
public class RestEndpointApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestEndpointApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService, RoleService roleService) {
		return args -> {
			Role roleAdmin = roleService.save(new Role(Role.ROLE_ADMIN));
			Role roleUser = roleService.save(new Role(Role.ROLE_USER));

			List<Role> roles1 = new ArrayList<>();
			roles1.add(roleAdmin);

			List<Role> roles2 = new ArrayList<>();
			roles2.add(roleUser);

			userService.create(new RegistrationDto("DJAltair", "djaltair", "1234"), roles1);
			userService.create(new RegistrationDto("Paloczek", "redskittlefox", "4321"), roles2);
		};
	}

}
