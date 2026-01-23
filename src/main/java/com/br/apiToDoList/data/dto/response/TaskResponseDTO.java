package com.br.apiToDoList.data.dto.response;

import com.br.apiToDoList.data.entity.Task;

public record TaskResponseDTO(
    Long id,
    String name, 
    String description,
    String situation

) {
    public TaskResponseDTO(Task tasks) {
        this(tasks.getIdTask(), tasks.getName(), tasks.getDescription(), tasks.getStatus());
    }
}
