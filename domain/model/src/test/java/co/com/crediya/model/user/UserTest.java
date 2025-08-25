package co.com.crediya.model.user;

import co.com.crediya.model.user.valueobjects.Birthday;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.model.user.valueobjects.Salary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

    private UUID userId;
    private String name;
    private String lastName;
    private Birthday birthday;
    private String address;
    private Email email;
    private Salary baseSalary;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        name = "Juan";
        lastName = "Pérez";
        birthday = new Birthday(LocalDate.of(1990, 5, 15));
        address = "Calle 123 #45-67";
        email = new Email("juan.perez@example.com");
        baseSalary = new Salary(new BigDecimal("3000000"));
    }

    @Test
    @DisplayName("shouldCreateUserWhenValidParametersProvided")
    void shouldCreateUserWhenValidParametersProvided() {
        User user = User.create(userId, name, lastName, birthday, address, email, baseSalary);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getBirthday()).isEqualTo(birthday);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getBaseSalary()).isEqualTo(baseSalary);
    }

    @Test
    @DisplayName("shouldCreateUserCopyWithNewAddressEmailSalary")
    void shouldCreateUserCopyWithNewAddressEmailSalary() {
        // Given
        User originalUser = User.create(userId, name, lastName, birthday, address, email, baseSalary);
        String newAddress = "Nueva Calle 456 #78-90";
        Email newEmail = new Email("nuevo.email@example.com");
        Salary newSalary = new Salary(new BigDecimal("4000000"));
        User updatedUser = originalUser.withAddressEmailSalary(newAddress, newEmail, newSalary);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(originalUser.getId());
        assertThat(updatedUser.getName()).isEqualTo(originalUser.getName());
        assertThat(updatedUser.getLastName()).isEqualTo(originalUser.getLastName());
        assertThat(updatedUser.getBirthday()).isEqualTo(originalUser.getBirthday());
        assertThat(updatedUser.getAddress()).isEqualTo(newAddress);
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        assertThat(updatedUser.getBaseSalary()).isEqualTo(newSalary);
        assertThat(originalUser.getAddress()).isEqualTo(address);
        assertThat(originalUser.getEmail()).isEqualTo(email);
        assertThat(originalUser.getBaseSalary()).isEqualTo(baseSalary);
    }

    @Test
    @DisplayName("shouldCreateUserUsingBuilder")
    void shouldCreateUserUsingBuilder() {
        User user = User.builder()
                .id(userId)
                .name(name)
                .lastName(lastName)
                .birthday(birthday)
                .address(address)
                .email(email)
                .baseSalary(baseSalary)
                .build();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getLastName()).isEqualTo(lastName);
        assertThat(user.getBirthday()).isEqualTo(birthday);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getBaseSalary()).isEqualTo(baseSalary);
    }

    @Test
    @DisplayName("shouldModifyUserUsingToBuilder")
    void shouldModifyUserUsingToBuilder() {
        User originalUser = User.create(userId, name, lastName, birthday, address, email, baseSalary);
        String newName = "Carlos";
        User modifiedUser = originalUser.toBuilder()
                .name(newName)
                .build();
        assertThat(modifiedUser.getName()).isEqualTo(newName);
        assertThat(modifiedUser.getId()).isEqualTo(originalUser.getId());
        assertThat(originalUser.getName()).isEqualTo(name);
    }
}