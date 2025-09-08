package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateUserRequest;
import co.com.crediya.api.dto.EditUserRequest;
import co.com.crediya.api.dto.UserResponse;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.valueobjects.CreateUserCommand;
import co.com.crediya.model.user.valueobjects.EditUserCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "birthday",   expression = "java(Birthday.of(dto.birthday()))")
    @Mapping(target = "email",      expression = "java(new Email(dto.email()))")
    @Mapping(target = "baseSalary", expression = "java(new Salary(dto.baseSalary()))")
    CreateUserCommand toCommand(CreateUserRequest dto);

    @Mapping(target = "email",      expression = "java(new Email(request.email()))")
    @Mapping(target = "baseSalary", expression = "java(new Salary(request.baseSalary()))")
    EditUserCommand toCommand(EditUserRequest request);

    @Mapping(target = "email",      expression = "java(user.getEmail().value())")
    @Mapping(target = "baseSalary", expression = "java(user.getBaseSalary().amount())")
    @Mapping(target = "birthday",   expression = "java(user.getBirthday().value())")
    UserResponse toResponse(User user);
}