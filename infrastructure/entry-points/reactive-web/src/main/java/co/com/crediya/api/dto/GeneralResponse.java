
package co.com.crediya.api.dto;

public record GeneralResponse<T>(
        int status,
        T data,
        Object error
) {}

