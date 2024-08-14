package tech.pedropires.springsecurity.controller;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.pedropires.springsecurity.domain.repository.RoleRepository;
import tech.pedropires.springsecurity.domain.repository.UserRepository;
import tech.pedropires.springsecurity.domain.users.Role;
import tech.pedropires.springsecurity.domain.users.User;
import tech.pedropires.springsecurity.dto.CreateUserDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController()
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor for UserController
     *
     * @param userRepository the user repository
     * @param roleRepository the role repository of the user
     * @param passwordEncoder the password encoder to encode the password
     */
    public UserController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Function to create a new basic user
     * @param createUserDto the data to create a new user
     * @return a response entity with the status of the request
     */
    @Transactional
    @PostMapping("/new-user")
    public ResponseEntity<Void> newBasicUser(@RequestBody CreateUserDto createUserDto) {
        // Get the role of the user - BASIC
        Role basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        // Check if the username already exists
        Optional<User> userExist = userRepository.findByUsername(createUserDto.username());
        if (userExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User already exists");
        }
        // Create a new basic user
        User user = new User(createUserDto.username(), passwordEncoder.encode(createUserDto.password()), Set.of(basicRole));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
