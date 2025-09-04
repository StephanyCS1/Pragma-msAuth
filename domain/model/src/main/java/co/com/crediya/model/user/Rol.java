package co.com.crediya.model.user;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Rol {

    private UUID id;
    private String rol;
    private String description;


}
