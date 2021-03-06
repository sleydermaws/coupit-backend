package com.allcode.coupit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.allcode.coupit.models.Role;
import com.allcode.coupit.models.User;
import com.allcode.coupit.repositories.UserRepository;
import com.allcode.coupit.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CoupitApplication {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@PostConstruct
	public void init(){
		Role userRole = new Role("ROLE_ADMIN");
		Role savedRole = roleRepository.save(userRole);
		User user = new User(
				"Admin",
				"Coupit",
				"admin@coupit.com",
				passwordEncoder.encode("password"),
				savedRole
				);

		if (userRepository.findByEmail(user.getEmail()) == null){
			userRepository.save(user);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(CoupitApplication.class, args);
	}
}
