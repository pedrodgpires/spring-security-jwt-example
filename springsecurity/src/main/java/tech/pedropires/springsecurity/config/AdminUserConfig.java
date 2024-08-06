package tech.pedropires.springsecurity.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tech.pedropires.springsecurity.domain.repository.RoleRepository;
import tech.pedropires.springsecurity.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import tech.pedropires.springsecurity.domain.users.Role;
import tech.pedropires.springsecurity.domain.users.RoleValues;
import tech.pedropires.springsecurity.domain.users.User;


import java.util.Optional;
import java.util.Set;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository,
                           UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        Role roleAdmin = roleRepository.findByName(RoleValues.ADMIN.name());

        Optional<User> userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
                user -> System.out.println("Admin user already exists"),
                () -> {
                    var user = new User();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(roleAdmin));
                    userRepository.save(user);
                }
        );
    }



}
