package tech.pedropires.springsecurity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.pedropires.springsecurity.dto.LoginRequestDto;
import tech.pedropires.springsecurity.dto.LoginResponseDto;
import tech.pedropires.springsecurity.service.TokenService;

/**
 * This class is a REST controller that handles requests related to tokens.
 * It is used to login the user.
 */
@RestController
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * This method is used to login the user
     * It receives a LoginRequestDto object and returns a LoginResponseDto object
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = tokenService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }

}

