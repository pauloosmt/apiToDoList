package com.br.apiToDoList.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.apiToDoList.data.dto.request.TaskRequestDTO;
import com.br.apiToDoList.data.dto.request.TaskUpdateDTO;
import com.br.apiToDoList.data.dto.response.ErrorResponseDTO;
import com.br.apiToDoList.data.dto.response.TaskResponseDTO;
import com.br.apiToDoList.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("task")
@Tag(name= "Tarefas", description = "Endpoints para gerenciamento de tarefas")

public class TaskController {

    
    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    @Operation(
        summary = "Criar uma nova tarefa",
        description = "Cria uma nova tarefa informando título, descrição e status."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso", content = @Content(mediaType = "application/json",schema = @Schema(implementation = TaskResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description =  "Usuário não autenticado", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequestDTO taskRequestDTO) {
        
        String emailUser = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(taskService.taskUserLogged(taskRequestDTO, emailUser));
    }

    @GetMapping("/all")
    @Operation(
        summary = "Listar todas as tarefas",
        description = "Retorna uma lista com todas as tarefas cadastradas."
    )
    @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    @ApiResponse(responseCode = "401", description =  "Usuário não autenticado", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class)))
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(Authentication authentication) {
        String email = authentication.getName();
        
        return ResponseEntity.status(HttpStatus.OK).body(taskService.allTasks(email));
    }

    @PutMapping(value = "/update/{idTask}")
    @Operation(
        summary = "Atualizar uma tarefa",
        description = "Atualiza os dados de uma tarefa existente."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarefa atualizada"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description =  "Usuário não autenticado", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "400", description =  "Dados Inválidos", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class))),
    })

    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long idTask, @RequestBody @Valid TaskUpdateDTO taskUpdateRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.updateTask(idTask, taskUpdateRequestDTO));
    }

    @GetMapping("/{idTask}")
    @Operation(
        summary = "Buscar tarefa por ID",
        description = "Retorna os dados de uma tarefa específica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description =  "Usuário não autenticado", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    public ResponseEntity<TaskResponseDTO> idTask(@PathVariable Long idTask) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getTaskByID(idTask));
    }

    @DeleteMapping("/delete/{idTask}")
    @Operation(
        summary = "Excluir uma tarefa",
        description = "Remove uma tarefa pelo ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarefa removida", content = @Content(
            mediaType = "application/json",
            schema = @Schema(
            type = "string", example = "The task with the ID '16' was deleted!"))),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class))),
        @ApiResponse(responseCode = "401", description =  "Usuário não autenticado", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    public ResponseEntity<String> deleteTask(@PathVariable Long idTask){
        return ResponseEntity.status(HttpStatus.OK).body(taskService.deleteTask(idTask));
    }

    @Operation(
        summary = "Buscar tarefa por Status",
        description = "Retorna todas as tarefas com algum determinado status (to_do, done, doing)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tarefas do status requisitado"),
        @ApiResponse(responseCode = "401", description =  "Usuário não autenticado", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    @GetMapping("all/{status}")
    public ResponseEntity<List<TaskResponseDTO>> taskByStatus(@PathVariable String status, Authentication authentication) {
        String email = authentication.getName();

        return ResponseEntity.status(HttpStatus.OK).body(taskService.taskByStatus(status, email));
    }

}