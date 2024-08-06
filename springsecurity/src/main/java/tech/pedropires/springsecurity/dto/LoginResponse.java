package tech.pedropires.springsecurity.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
