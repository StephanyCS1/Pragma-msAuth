package co.com.crediya.api.security;

import co.com.crediya.model.user.gateways.PasswordEncodePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncodePort {

    private final org.springframework.security.crypto.password.PasswordEncoder delegate;

    @Override
    public String encode(String raw) {
        return delegate.encode(raw);
    }

    @Override
    public boolean matches(String raw, String encoded) {
        return delegate.matches(raw, encoded);
    }
}