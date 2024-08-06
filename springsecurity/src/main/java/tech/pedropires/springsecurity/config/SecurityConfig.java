package tech.pedropires.springsecurity.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.hibernate.annotations.Immutable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * This class is used to configure the security of the application
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * The public key is used to verify the JWT token signature
     */
    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    /**
     * The private key is used to sign the JWT token
     */
    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    /**
     * This method is used to create a SecurityFilterChain bean that will be used to configure the security of the application
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize                        // AUTHORIZE REQUESTS
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()    // LOGIN ENDPOINT MUST BE PERMITTED
                        .anyRequest().authenticated())                               // ANY OTHER REQUEST MUST BE AUTHENTICATED
                .csrf(csrf -> csrf.disable())   // ONLY FOR TESTING PURPOSES
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))  // JWT TOKEN
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // STATELESS SESSION

        return http.build();
    }

    /**
     * This method is used to create a JwtDecoder bean that will be used to verify the JWT token signature
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * This method is used to create a JwtEncoder bean that will be used to sign the JWT token
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        // Create a JWK object with the private key
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(privateKey).build();
        // Create a JWKSet object with the JWK object
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        // Create a NimbusJwtEncoder object with the JWKSet object
        return new NimbusJwtEncoder(jwks);
    }

    /**
     * This method is used to create a BCryptPasswordEncoder bean that will be used to encode the user password
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
