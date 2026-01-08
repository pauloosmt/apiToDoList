package com.br.apiToDoList.data.entity;

import com.br.apiToDoList.data.dto.request.TaskRequestDTO;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tasks")
public class Tasks {

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


    @Builder
    public Tasks(TaskRequestDTO taskRequestDTO, User user) {
        this.name = taskRequestDTO.name();
        this.description = taskRequestDTO.description();
        this.status = taskRequestDTO.status();
        this.user = user;
    }
}
