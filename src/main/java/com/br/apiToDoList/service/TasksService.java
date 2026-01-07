package com.br.apiToDoList.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.apiToDoList.data.dto.response.TasksResponseDTO;
import com.br.apiToDoList.data.entity.Tasks;
import com.br.apiToDoList.repository.TasksRepository;

@Service
public class TasksService {

    @Autowired
    public TasksRepository tasksRepository;

    public TasksResponseDTO getTaskByID(Long idTask) {
        Tasks task = findTaskById(idTask);
        return new TasksResponseDTO(task);

    }


    private Tasks findTaskById(Long idTask) {
        return tasksRepository.findById(idTask).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}