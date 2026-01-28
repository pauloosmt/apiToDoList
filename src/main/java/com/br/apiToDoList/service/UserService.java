package com.br.apiToDoList.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.br.apiToDoList.data.dto.request.UserRequestDTO;
import com.br.apiToDoList.data.dto.response.UserResponseDTO;
import com.br.apiToDoList.data.entity.Task;
import com.br.apiToDoList.data.entity.User;
import com.br.apiToDoList.data.entity.UserRole;
import com.br.apiToDoList.repository.TaskRepository;
import com.br.apiToDoList.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long idUser) {
        User user = findUserById(idUser);
        return new UserResponseDTO(user);
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO, String password, UserRole userRole) {
        
        User user = new User(userRequestDTO, password, userRole);
        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    public UserResponseDTO updateUser(Long idUser, UserRequestDTO usersRequestDTO) {
        User user = findUserById(idUser);

        if(usersRequestDTO.name()!= null && !usersRequestDTO.name().isBlank()) {
            user.setName(usersRequestDTO.name());
        }

        if(usersRequestDTO.email() != null && !usersRequestDTO.email().isBlank()) {
            if(usersRequestDTO.email().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
                user.setEmail(usersRequestDTO.email());
        }

        if(usersRequestDTO.password() != null && !usersRequestDTO.password().isBlank()) {
            String encryptedSenha = new BCryptPasswordEncoder().encode(usersRequestDTO.password());
            user.setPassword(encryptedSenha);
        }

        userRepository.save(user);

        return new UserResponseDTO(user);

    }

    public String deleteUser(Long idUser) {
        User user = findUserById(idUser);
        
        List<Task> allUserTask = taskRepository.findByUserEmail(user.getEmail());

        taskRepository.deleteAll(allUserTask);
        userRepository.delete(user);

        return "The user with email '"+ user.getEmail() + "' has been removed.";
    }
    



    private User findUserById(Long idUser) {
        return userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
