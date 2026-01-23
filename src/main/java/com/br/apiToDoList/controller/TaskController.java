package com.br.apiToDoList.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.apiToDoList.data.dto.request.TaskRequestDTO;
import com.br.apiToDoList.data.dto.response.TaskResponseDTO;
import com.br.apiToDoList.service.TaskService;

@RestController
@RequestMapping("task")
public class TaskController {

    

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody TaskRequestDTO taskRequestDTO) {
        
        String emailUser = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(taskService.taskUserLogged(taskRequestDTO, emailUser));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(Authentication authentication) {
        String email = authentication.getName();
        
        return ResponseEntity.status(HttpStatus.OK).body(taskService.allTasks(email));
    }

    @PutMapping(value = "/update/{idTask}")

    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long idTask, @RequestBody TaskRequestDTO taskRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.updateTask(idTask, taskRequestDTO));
    }

}
