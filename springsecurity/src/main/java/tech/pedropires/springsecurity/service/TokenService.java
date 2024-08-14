package tech.pedropires.springsecurity.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import tech.pedropires.springsecurity.domain.repository.UserRepository;
import tech.pedropires.springsecurity.domain.users.Role;
import tech.pedropires.springsecurity.domain.users.User;
import tech.pedropires.springsecurity.dto.LoginRequestDto;
import tech.pedropires.springsecurity.dto.LoginResponseDto;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenService {


    private final JwtEncoder jwtEncoder;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private TokenService(JwtEncoder jwtEncoder,
                         UserRepository userRepository,
                         BCryptPasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * This method is used to login the user
     * It receives a LoginRequestDto object and returns a LoginResponseDto object
     */
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        // Find the user by username
        Optional<User> user = userRepository.findByUsername(loginRequestDto.username());
        // Check if the user exists and if the password is correct
        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequestDto, passwordEncoder)) {
            throw new BadCredentialsException("User or password is invalid!");
        }
        // Create instant and expiresIn for the token
        Instant now = Instant.now();
        long expiresIn = 300L;
        // Create the scopes for the token - scopes are the permissions that the user has
        String scopes = user.get().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));
        // Create the claims for the token - claims are the data that is stored in the token
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("mybackend")                         // The issuer of the token
                .subject(user.get().getUserId().toString())  // The subject of the token
                .issuedAt(now)                               // The time the token was issued
                .expiresAt(now.plusSeconds(expiresIn))       // The time the token expires
                .claim("scope", scopes)                // The scopes of the token
                .build();                                    // Build the claims
        // Encode the claims into a token
        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        // Return the token
        return new LoginResponseDto(jwtValue, expiresIn);
    }
}
