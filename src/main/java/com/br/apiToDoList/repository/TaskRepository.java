package com.br.apiToDoList.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.br.apiToDoList.data.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{

    @Query("SELECT t FROM Tasks t WHERE t.user.email = :email")
    List<Task> findByUserEmail(String email);
}
