package co.com.crediya.model.user;

import co.com.crediya.model.user.valueobjects.Birthday;
import co.com.crediya.model.user.valueobjects.Email;
import co.com.crediya.model.user.valueobjects.Salary;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String name;
    private String lastName;
    private Birthday birthday;
    private String address;
    private Email email;
    private Salary baseSalary;


    public static User create(UUID id, String name, String lastName, Birthday birthday,
                              String address, Email email, Salary baseSalary) {
        return new User(id,name, lastName, birthday, address, email, baseSalary);
    }

    public User withAddressEmailSalary(String newAddress, Email newEmail, Salary newSalary) {
        return new User(
                this.id,
                this.name,
                this.lastName,
                this.birthday,
                newAddress,
                newEmail,
                newSalary
        );
    }
}