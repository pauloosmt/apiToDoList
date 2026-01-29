package com.br.apiToDoList.data.entity;

import java.time.LocalDate;

import com.br.apiToDoList.data.dto.request.TaskRequestDTO;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTask;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false) 
    private String status;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;

    private LocalDate dataTask;


    @Builder
    public Task(TaskRequestDTO taskRequestDTO, User user) {
        this.name = taskRequestDTO.name();
        this.description = taskRequestDTO.description();
        this.status = taskRequestDTO.status();
        this.user = user;
    }
}
