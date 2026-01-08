package com.br.apiToDoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.br.apiToDoList.data.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
}
