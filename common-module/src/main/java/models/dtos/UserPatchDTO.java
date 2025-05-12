package models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDTO {

    @Size(min = 5, max = 50, message = "Username must be at least 5 and at most 50 characters long")
    private String username;

    @Size(min = 2, max = 50, message = "First name must be at least 2 and at most 50 characters long")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be at least 2 and at most 50 characters long")
    private String lastName;

    @Email(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;

    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format"
    )
    private String phone;

    private LocalDate birthDate;
}
