package co.com.crediya.model.user.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public enum RolName {

    ADMIN(UUID.fromString("b34c1721-c4c2-42da-907c-aed4cd00788c")),
    USER(UUID.fromString("4595846d-823f-466a-9ac9-b9707c27dd18")),
    ASESOR(UUID.fromString("51688f39-44c2-4216-a0aa-bd0351b79dd0"));

    private final UUID id;

    RolName(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    private static final Map<UUID, RolName> BY_ID =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(RolName::getId, e -> e));

    private static final Map<String, RolName> BY_NAME =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(Enum::name, e -> e));

    public static Optional<RolName> fromId(UUID id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    public static Optional<RolName> fromRol(String name) {
        if (name == null) return Optional.empty();
        try {
            return Optional.of(RolName.valueOf(name.trim().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
