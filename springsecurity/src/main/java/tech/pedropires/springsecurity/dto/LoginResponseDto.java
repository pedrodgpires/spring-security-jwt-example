package tech.pedropires.springsecurity.dto;

public record LoginResponseDto(String accessToken, Long expiresIn) {
}
