package com.br.apiToDoList.data.dto.request;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para cadastro ou atualização de um usuário")
public record UserRequestDTO(
    @Schema(description = "Nome completo do usuário", example = "Paulo Taciano")
    @NotBlank(message = "Name is required")
    String name,

    @Schema(description = "Email do usuário", example = "paulo@email.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    String email,

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "The password must be at least 6 characters long")
    String password

    
    
) {
 
}
