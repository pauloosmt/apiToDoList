package com.br.apiToDoList.service;



import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


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

        task.setDataTask(LocalDate.now());

        taskRepository.save(task);
           
        return new TaskResponseDTO(task);
        
    }

    public List<TaskResponseDTO> allTasks(String email) {
        List<Task> tasks = taskRepository.findByUserEmail(email);

        return tasks.stream().map(TaskResponseDTO::new).collect(Collectors.toList());
        
    }

    public TaskResponseDTO updateTask(Long idTask, TaskRequestDTO taskRequestDTO) {
        Task task = findTaskById(idTask);
        if(taskRequestDTO.name() != null && !taskRequestDTO.name().isBlank()) {
            task.setName(taskRequestDTO.name());
        } 
        if(taskRequestDTO.status() != null && !taskRequestDTO.status().isBlank()) {
            task.setStatus(taskRequestDTO.status());
        }
        if(taskRequestDTO.description() != null && !taskRequestDTO.description().isBlank()) {
            task.setDescription(taskRequestDTO.description());
        }

        taskRepository.save(task);

        return new TaskResponseDTO(task);
    }




    private Task findTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}