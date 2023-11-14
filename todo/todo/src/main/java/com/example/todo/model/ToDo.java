package com.example.todo.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import lombok.Data;

@Entity
@Data
public class ToDo {
    @Id
    @GeneratedValue
    int id;
    String title;
    String content;
    boolean completed;
    LocalTime checkTime;
    LocalDateTime itemTime;

    @PrePersist
    public void createdAt() {
        this.itemTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }
    // 이슈 작성일이 해당 일이라면....
    // 해당 요일의 데이 이슈는 위크 이슈의 요일별로 묶여야한다. ==월(week,day) 이슈도 같은 로직

}