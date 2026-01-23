package com.br.apiToDoList.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.apiToDoList.data.dto.request.TaskRequestDTO;
import com.br.apiToDoList.data.dto.response.TaskResponseDTO;
import com.br.apiToDoList.data.entity.Task;
import com.br.apiToDoList.data.entity.User;
import com.br.apiToDoList.repository.TaskRepository;
import com.br.apiToDoList.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public TaskResponseDTO getTaskByID(Long idTask) {
        Task task = findTaskById(idTask);
        return new TaskResponseDTO(task);

    }

    public TaskResponseDTO taskUserLogged(TaskRequestDTO taskRequestDTO, String email) {
        User user = (User) userRepository.findByEmail(email);

        Task task = new Task(taskRequestDTO, user);

        taskRepository.save(task);
           
        return new TaskResponseDTO(task);
        
    }

    private Task findTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}