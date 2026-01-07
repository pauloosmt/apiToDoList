package com.br.apiToDoList.data.dto.response;

import com.br.apiToDoList.data.entity.Tasks;

public record TasksResponseDTO(
    Long id,
    String name, 
    String description,
    String situation

) {
    public TasksResponseDTO(Tasks tasks) {
        this(tasks.getIdTask(), tasks.getName(), tasks.getDescription(), tasks.getStatus());
    }
}
