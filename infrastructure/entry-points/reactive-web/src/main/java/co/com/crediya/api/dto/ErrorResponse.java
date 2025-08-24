package co.com.crediya.api.dto;

public record ErrorResponse(
        String code,
        String message,
        Object details
) {}