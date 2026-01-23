package com.br.apiToDoList.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.apiToDoList.data.dto.response.TaskResponseDTO;
import com.br.apiToDoList.data.entity.Task;
import com.br.apiToDoList.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    public TaskRepository taskRepository;

    public TaskResponseDTO getTaskByID(Long idTask) {
        Task task = findTaskById(idTask);
        return new TaskResponseDTO(task);

    }


    private Task findTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}