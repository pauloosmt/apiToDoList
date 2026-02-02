package com.br.apiToDoList.data.dto.request;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados necessários para criar ou atualizar uma tarefa")
public record TaskRequestDTO(

    @Schema(description = "Título da tarefa", example = "Estudar o Projeto do ZettaLab")
    @NotBlank(message = "Name is required")
    String name, 

    @Schema(description = "Status da tarefa")
    @Pattern(regexp = "^(to_do|doing|done)$", message = "Situation must be 'to_do', 'doing' or 'done'")
    String status,

    @Schema(description = "Descrição da tarefa", example = "Documentar a API corretamente")
    String description

) {

}
