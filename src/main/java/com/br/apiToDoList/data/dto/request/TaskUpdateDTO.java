package com.br.apiToDoList.data.dto.request;

import jakarta.validation.constraints.Pattern;

public record TaskUpdateDTO (
    String name,

    @Pattern(regexp = "^(to_do|doing|done)$", message = "Situation must be 'to_do', 'doing' or 'done'")
    String status,

    String description
) {

}
