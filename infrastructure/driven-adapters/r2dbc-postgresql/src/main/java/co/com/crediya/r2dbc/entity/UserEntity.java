package co.com.crediya.r2dbc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "user_entity")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("last_name")
    private String lastName;

    @Column("birthday")
    private LocalDate birthday;

    @Column("address")
    private String address;

    @Column("email")
    private String email;

    @Column("base_salary")
    private BigDecimal baseSalary;

    @Column("identification")
    private String identification;

    @Column("password")
    private String password;

    @Column("rol")
    private UUID rol;



}