package co.com.crediya.model.user.gateways;

public interface PasswordEncodePort {
    String encode(String raw);
    boolean matches(String raw, String encoded);
}
