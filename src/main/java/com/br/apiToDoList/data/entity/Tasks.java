package com.br.apiToDoList.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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
}
