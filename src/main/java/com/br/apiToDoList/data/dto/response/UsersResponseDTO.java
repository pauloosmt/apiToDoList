package com.br.apiToDoList.data.dto.response;

import com.br.apiToDoList.data.entity.Users;

public record UsersResponseDTO(

    Long id,
    String name,
    String email,
    String password
) {
    public UsersResponseDTO(Users users) {
        this(users.getIdUser(), users.getName(), users.getEmail(), users.getPassword());
    }
}
