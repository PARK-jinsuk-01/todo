package com.example.todo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.todo.model.ToDo;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Integer> {
    List<ToDo> findByItemTimeBetween(LocalDateTime start, LocalDateTime end);
    List<ToDo> findByOrderByItemTimeAsc();
}
