package com.example.todo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.todo.model.ToDo;
import com.example.todo.repository.ToDoRepository;

@Controller
public class ToDoListController {

    @Autowired
    ToDoRepository toDoRepository;

    @ResponseBody
    @GetMapping("/itemlist")
    public Map<String, Object> getItemList() {
        Map<String, Object> map = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        try {
            List<ToDo> todaysToDos = toDoRepository.findByItemTimeBetween(startOfDay, endOfDay);
            todaysToDos.sort(Comparator.comparing(ToDo::getItemTime));
            List<ToDo> completedItems = todaysToDos.stream()
                    .filter(ToDo::isCompleted)
                    .collect(Collectors.toList());
            List<ToDo> incompleteItems = todaysToDos.stream()
                    .filter(item -> !item.isCompleted())
                    .collect(Collectors.toList());

            map.put("completedItems", completedItems);
            map.put("incompleteItems", incompleteItems);
            map.put("result", true);
        } catch (Exception e) {
            map.put("msg", "데이터를 불러오는 중 오류가 발생했습니다.");
            map.put("result", false);
        }
        return map;
    }

    // 세이브 컨트롤러
    @ResponseBody
    @PostMapping("/item")
    public Map<String, Object> item(@RequestBody Map<String, Object> item) {
        Map<String, Object> map = new HashMap<>();
        String title = (String) item.get("title");
        String content = (String) item.get("content");
        String time = (String) item.get("time");
    
        if (isNullOrEmpty(time)) {
            return createErrorResponse(map, "시간을 입력해주세요.");
        }
    
        if (isNullOrEmpty(title)) {
            return createErrorResponse(map, "제목을 입력해주세요.");
        }
    
        if (isNullOrEmpty(content)) {
            return createErrorResponse(map, "내용을 작성해주세요.");
        }
    
        try {
            ToDo todo = new ToDo();
            todo.setTitle(title);
            todo.setContent(content);
            todo.setCheckTime(LocalTime.parse(time));
            toDoRepository.save(todo);
            map.put("msg", "리스트를 작성했습니다.");
            map.put("result", true);
        } catch (Exception e) {
            map.put("msg", "작성중 오류가 발생했습니다.");
            map.put("result", false);
        }
        return map;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    private Map<String, Object> createErrorResponse(Map<String, Object> map, String message) {
        map.put("msg", "오류가 발생했습니다.");
        map.put("result", false);
        return map;
    }

    // 삭제 컨트롤러
    @ResponseBody
    @DeleteMapping("/item/{id}")
    public Map<String, Object> deleteItem(@PathVariable Integer id) {
        Map<String, Object> map = new HashMap<>();

        try {
            if (toDoRepository.existsById(id)) {
                toDoRepository.deleteById(id);
                map.put("msg", "삭제에 성공했습니다.");
            } else {
                map.put("msg", "삭제 중 오류가 발생했습니다.");
            }

        } catch (Exception e) {
            map.put("msg", "삭제 중 오류가 발생했습니다. : " + e.getMessage());
        }
        return map;
    }

    @ResponseBody
    @PutMapping("/put/{id}")
    public ResponseEntity<ToDo> putCheck(@PathVariable Integer id, @RequestBody ToDo updatedTodo) {
        Optional<ToDo> optionalTodoItem = toDoRepository.findById(id);

        if (!optionalTodoItem.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ToDo todoItem = optionalTodoItem.get();
        todoItem.setCompleted(updatedTodo.isCompleted());
        toDoRepository.save(todoItem);

        return ResponseEntity.ok(todoItem);
    }
}
