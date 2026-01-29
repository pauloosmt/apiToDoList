package com.br.apiToDoList.data.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Resposta padrão para erros da API")
public class ErrorResponseDTO {

    
    private int status;

    
    private String message;

    
    private String timestamp;
}