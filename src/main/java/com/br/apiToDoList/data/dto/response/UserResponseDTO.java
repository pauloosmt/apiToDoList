package com.br.apiToDoList.data.dto.response;



import com.br.apiToDoList.data.entity.User;
import com.br.apiToDoList.data.entity.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta contendo os dados do usuário")
public record UserResponseDTO(
    @Schema(description = "ID do usuário", example = "1")
    Long id,

    @Schema(description = "Nome do usuário", example = "Paulo Taciano")
    String name,

    @Schema(description = "Email do usuário", example = "paulo@email.com")
    String email,

    @Schema(description = "Senha Criptografada do Usuário com JWT")
    String password,

    @Schema(description = "Perfil de Usuário", example = "USER")
    UserRole role
    
) {
    public UserResponseDTO(User user) {
        this(user.getIdUser(), user.getName(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
