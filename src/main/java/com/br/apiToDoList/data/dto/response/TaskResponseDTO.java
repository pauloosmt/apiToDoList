package com.br.apiToDoList.data.dto.response;

import java.time.LocalDate;

import com.br.apiToDoList.data.entity.Task;

public record TaskResponseDTO(
    Long id,
    String name, 
    String description,
    String situation,
    LocalDate dataTask

) {
    public TaskResponseDTO(Task tasks) {
        this(tasks.getIdTask(), tasks.getName(), tasks.getDescription(), tasks.getStatus(), tasks.getDataTask());
    }
}
