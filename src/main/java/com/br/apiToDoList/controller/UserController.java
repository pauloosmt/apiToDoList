package com.br.apiToDoList.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.apiToDoList.data.dto.request.UserRequestDTO;
import com.br.apiToDoList.data.dto.response.UserResponseDTO;
import com.br.apiToDoList.data.entity.UserRole;
import com.br.apiToDoList.repository.UserRepository;
import com.br.apiToDoList.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        if(this.userRepository.findByEmail(userRequestDTO.email()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String password = new BCryptPasswordEncoder().encode(userRequestDTO.password());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO, password, UserRole.USER));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> allUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UserResponseDTO> userByID(@PathVariable Long idUser) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(idUser));
    }

    @PutMapping("/update/{idUser}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long idUser, @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(idUser, userRequestDTO));
    }

    @DeleteMapping("/delete/{idUser}")
    public ResponseEntity<String> deleteUser(@PathVariable Long idUser) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(idUser));
    }
}