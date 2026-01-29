package com.br.apiToDoList.data.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta da autenticação com token JWT")
public record LoginResponseDTO(String token) {
    
}
