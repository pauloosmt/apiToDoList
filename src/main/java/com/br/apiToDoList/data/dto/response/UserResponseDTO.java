package com.br.apiToDoList.data.dto.response;



import com.br.apiToDoList.data.entity.User;
import com.br.apiToDoList.data.entity.UserRole;

public record UserResponseDTO(

    Long id,
    String name,
    String email,
    String password,
    UserRole role
    
) {
    public UserResponseDTO(User user) {
        this(user.getIdUser(), user.getName(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
