package com.br.apiToDoList.data.dto.request;

import com.br.apiToDoList.data.entity.Users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TaskRequestDTO(

    @NotBlank(message = "Name is required")
    String name, 

    @Pattern(regexp = "^(to do|doing|done)$", message = "Situation must be 'to do', 'doing' or 'done'")
    String status,

    String description

) {

}
