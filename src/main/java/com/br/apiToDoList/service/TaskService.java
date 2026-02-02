package com.br.apiToDoList.service;



import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.br.apiToDoList.data.dto.request.TaskRequestDTO;
import com.br.apiToDoList.data.dto.request.TaskUpdateDTO;
import com.br.apiToDoList.data.dto.response.TaskResponseDTO;
import com.br.apiToDoList.data.entity.Task;
import com.br.apiToDoList.data.entity.User;
import com.br.apiToDoList.exceptions.entity.EntityNotFoundException;
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

    public List<TaskResponseDTO> taskByStatus(String status, String email) {
        List<Task> tasksUser = taskRepository.findByUserEmail(email);

        return tasksUser.stream().filter(
                task -> task.getStatus().equalsIgnoreCase(status)
        ).map(TaskResponseDTO::new).collect(Collectors.toList());
    }

    public TaskResponseDTO updateTask(Long idTask, TaskUpdateDTO taskUpdateRequestDTO) {
        Task task = findTaskById(idTask);
        if(taskUpdateRequestDTO.name() != null && !taskUpdateRequestDTO.name().isBlank()) {
            task.setName(taskUpdateRequestDTO.name());
        } 
        if(taskUpdateRequestDTO.status() != null && !taskUpdateRequestDTO.status().isBlank()) {
            task.setStatus(taskUpdateRequestDTO.status());
        }
        if(taskUpdateRequestDTO.description() != null && !taskUpdateRequestDTO.description().isBlank()) {
            task.setDescription(taskUpdateRequestDTO.description());
        }

        taskRepository.save(task);

        return new TaskResponseDTO(task);
    }

    public String deleteTask(Long idTask) {
        Task task = findTaskById(idTask);
        taskRepository.delete(task);

        return "The task with the ID '"+ task.getIdTask() + "' was deleted!";
    }



    private Task findTaskById(Long idTask) {
        return taskRepository.findById(idTask).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }
}