package com.br.apiToDoList.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(name="name", nullable = false, length = 45)
    private String name;

    @Column(name="email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name="password", nullable = false, length = 100)
    private String password;
}
