package com.br.apiToDoList.data.dto.response;

import java.time.LocalDate;

import com.br.apiToDoList.data.entity.Task;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Representa uma tarefa de um usuário do sistema")
public record TaskResponseDTO(
    @Schema(description = "ID da tarefa", example = "1")
    Long id,

    @Schema(description = "Título da tarefa", example = "Estudar o Projeto do ZettaLab")
    String name, 
    @Schema(description = "Descrição da tarefa", example = "Estudar Swagger e Java")
    String description,

    @Schema(description = "Status da tarefa", example = "TO DO | DONE | DOING")
    String situation,

    LocalDate dataTask

) {
    public TaskResponseDTO(Task tasks) {
        this(tasks.getIdTask(), tasks.getName(), tasks.getDescription(), tasks.getStatus(), tasks.getDataTask());
    }
}
