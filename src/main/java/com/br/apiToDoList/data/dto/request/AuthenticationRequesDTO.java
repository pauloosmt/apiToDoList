package com.br.apiToDoList.data.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação do usuário")
public record AuthenticationRequesDTO( 
    @Schema(description = "Email do usuário", example = "user@email.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    String email,

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank(message = "Password is required")
    String password

)
{}
