package co.com.crediya.api.handler;

import co.com.crediya.api.dto.GeneralResponse;
import co.com.crediya.model.user.exceptions.DomainValidationException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import reactor.core.publisher.Mono;

import java.util.Map;

public final class ErrorHandler {

    private static final String ERROR_KEY = "error";

    private ErrorHandler(){}

    public static HandlerFilterFunction<ServerResponse, ServerResponse> filter() {
        return (request, next) -> next.handle(request)
                .onErrorResume(ex -> mapException(request, ex));
    }

    private static Mono<ServerResponse> mapException(ServerRequest req, Throwable ex) {
        if (ex instanceof DomainValidationException dve) {
            return write(HttpStatus.BAD_REQUEST, dve.getMessage());
        }
        if (ex instanceof DecodingException de) {
            String msg = "JSON inválido o tipos incorrectos";
            Throwable cause = de.getCause();
            if (cause instanceof ValueInstantiationException vie && vie.getCause()!=null) {
                msg = vie.getCause().getMessage();
            }
            return write(HttpStatus.BAD_REQUEST, msg);
        }

        return write(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");
    }

    private static String safe(Throwable t) {
        return t == null ? "" : t.getMessage();
    }

    private static Mono<ServerResponse> write(HttpStatus status, String message) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new GeneralResponse<>(
                        status.value(),
                        null,
                        Map.of(ERROR_KEY, message)
                ));
    }
}