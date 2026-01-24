package com.br.apiToDoList.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.apiToDoList.data.dto.request.AuthenticationRequesDTO;
import com.br.apiToDoList.data.dto.request.UserRequestDTO;
import com.br.apiToDoList.data.dto.response.LoginResponseDTO;
import com.br.apiToDoList.data.dto.response.UserResponseDTO;
import com.br.apiToDoList.data.entity.User;
import com.br.apiToDoList.data.entity.UserRole;
import com.br.apiToDoList.infra.security.TokenService;
import com.br.apiToDoList.repository.UserRepository;
import com.br.apiToDoList.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
 

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationRequesDTO data) {
        var userPass = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authManager.authenticate(userPass);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRequestDTO data){
        if(this.userRepository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(data, encryptedPassword, UserRole.USER));
    }
}
