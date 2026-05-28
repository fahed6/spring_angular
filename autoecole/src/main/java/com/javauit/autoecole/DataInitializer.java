package com.javauit.autoecole;

import com.javauit.autoecole.entity.User;
import com.javauit.autoecole.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFullName("Admin");
            admin.setEmail("admin@autoecole.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);

            User staff = new User();
            staff.setFullName("Staff");
            staff.setEmail("staff@autoecole.com");
            staff.setPasswordHash(passwordEncoder.encode("staff123"));
            staff.setRole(User.Role.STAFF);
            userRepository.save(staff);

            System.out.println("Test users created: admin@autoecole.com / staff@autoecole.com");
        }
    }
}