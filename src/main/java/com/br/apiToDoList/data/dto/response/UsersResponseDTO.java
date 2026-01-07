package com.br.apiToDoList.data.dto.response;

public record UsersResponseDTO(

    Long id,
    String name,
    String email,
    String password
) {

}
