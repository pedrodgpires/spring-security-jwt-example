package tech.pedropires.springsecurity.controller;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.pedropires.springsecurity.domain.users.User;
import tech.pedropires.springsecurity.dto.CreateUserDto;
import tech.pedropires.springsecurity.service.UserService;

import java.util.List;

@RestController()
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    /**
     * Constructor for UserController
     *
     * @param userService the service to handle user operations
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Function to create a new basic user
     * @param createUserDto the data to create a new user
     * @return a response entity with the status of the request
     */
    @Transactional
    @PostMapping("/new")
    public ResponseEntity<Void> newBasicUser(@RequestBody CreateUserDto createUserDto) {
        boolean created = userService.newBasicUser(createUserDto);
        if (!created) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User already exists.");
        } else {
            return ResponseEntity.ok().build();
        }
    }


    @GetMapping("/list-all")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userService.listUsers();
        return ResponseEntity.ok(users);
    }
}
