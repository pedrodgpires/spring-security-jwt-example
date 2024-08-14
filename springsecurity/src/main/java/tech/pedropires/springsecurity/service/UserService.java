package tech.pedropires.springsecurity.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tech.pedropires.springsecurity.domain.repository.RoleRepository;
import tech.pedropires.springsecurity.domain.repository.UserRepository;
import tech.pedropires.springsecurity.domain.users.Role;
import tech.pedropires.springsecurity.domain.users.User;
import tech.pedropires.springsecurity.dto.CreateUserDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Function to create a new basic user
     *
     * @param createUserDto the data to create a new user
     * @return a response entity with the status of the request
     */
    public boolean newBasicUser(CreateUserDto createUserDto) {
        // Get the role of the user - BASIC
        Role basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        // Check if the username already exists
        Optional<User> userExist = userRepository.findByUsername(createUserDto.username());
        if (userExist.isPresent()) {
            return false;
        }
        // Create a new basic user
        User user = new User(createUserDto.username(), passwordEncoder.encode(createUserDto.password()), Set.of(basicRole));
        userRepository.save(user);
        return true;
    }

    /**
     * Function to list all users
     * But only if the user has the ADMIN role
     *
     * @return a list of all users
     */
    public List<User> listUsers() {
        return userRepository.findAll();
    }

}
