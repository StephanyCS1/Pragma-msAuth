package co.com.crediya.api.dto;

public record TokenResponse(
        String token,
        long   expiresIn,
        String tokenType
) {
}
