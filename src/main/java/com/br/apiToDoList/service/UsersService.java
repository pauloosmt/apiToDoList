package com.br.apiToDoList.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.apiToDoList.data.dto.request.UsersRequestDTO;
import com.br.apiToDoList.data.dto.response.UsersResponseDTO;
import com.br.apiToDoList.data.entity.Users;
import com.br.apiToDoList.repository.UsersRepository;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public List<UsersResponseDTO> getAllUsers() {
        List<Users> users = usersRepository.findAll();

        return users.stream().map(UsersResponseDTO::new).collect(Collectors.toList());
    }

    public UsersResponseDTO getUserById(Long idUser) {
        Users user = findUserById(idUser);
        return new UsersResponseDTO(user);
    }

    public UsersResponseDTO createUser(UsersRequestDTO usersRequestDTO, String password) {
        Users user = new Users(usersRequestDTO, password);
        usersRepository.save(user);

        return new UsersResponseDTO(user);
    }

    public UsersResponseDTO updateUsers(Long idUser, UsersRequestDTO usersRequestDTO) {
        Users user = findUserById(idUser);

        if(usersRequestDTO.name()!= null && !usersRequestDTO.name().isBlank()) {
            user.setName(usersRequestDTO.name());
        }

        if(usersRequestDTO.email() != null && !usersRequestDTO.email().isBlank()) {
            user.setEmail(usersRequestDTO.email());
        }

        usersRepository.save(user);

        return new UsersResponseDTO(user);

    }



    private Users findUserById(Long idUser) {
        return usersRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
