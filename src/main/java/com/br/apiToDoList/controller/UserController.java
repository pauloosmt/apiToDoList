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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("user")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
     @Operation(
            summary = "Cadastrar usuário",
            description = "Cria um novo usuário no sistema"
    )
    @ApiResponse(responseCode = "403", description =  "Usuário não autenticado")
    @ApiResponse(
            responseCode = "201",
            description = "Usuário criado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )
    )
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        if(this.userRepository.findByEmail(userRequestDTO.email()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String password = new BCryptPasswordEncoder().encode(userRequestDTO.password());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDTO, password, UserRole.USER));
    }

    @GetMapping("/all")
     @Operation(
            summary = "Listar usuários",
            description = "Retorna todos os usuários cadastrados"
    )
    @ApiResponse(responseCode = "403", description =  "Usuário não autenticado")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de usuários retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )
    )
    public ResponseEntity<List<UserResponseDTO>> allUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }

    @GetMapping("/{idUser}")
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados de um usuário específico"
    )
    @ApiResponse(responseCode = "403", description =  "Usuário não autenticado")
    @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )
    )
    public ResponseEntity<UserResponseDTO> userByID(@PathVariable Long idUser) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(idUser));
    }

    @PutMapping("/update/{idUser}")
    @Operation(summary = "Editar um usuário", description = "Edita io usuário do ID fornecido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "403", description =  "Usuário não autenticado"),
        @ApiResponse(responseCode = "409", description = "Dados ja existentes")
    })
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long idUser, @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(idUser, userRequestDTO));
    }

    @DeleteMapping("/delete/{idUser}")
    @Operation(
            summary = "Excluir usuário",
            description = "Remove um usuário pelo ID"
    )
    @ApiResponse(responseCode = "403", description =  "Usuário não autenticado")
    @ApiResponse(
            responseCode = "200",
            description = "Usuário removido com sucesso"
    )
    public ResponseEntity<String> deleteUser(@PathVariable Long idUser) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(idUser));
    }
}