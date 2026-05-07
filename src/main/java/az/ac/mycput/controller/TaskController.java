package az.ac.mycput.controller;

import az.ac.mycput.config.CurrentUser;
import az.ac.mycput.dto.AppDTO.TaskRequest;
import az.ac.mycput.dto.AppDTO.TaskStatusRequest;
import az.ac.mycput.entity.Task;
import az.ac.mycput.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;
    private final CurrentUser currentUser;

    @GetMapping
    public List<Task> getAll(@RequestParam(required = false) Long moduleId,
                              @RequestParam(required = false) Task.Status status) {
        return taskService.getTasksForUser(currentUser.get(), moduleId, status);
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(currentUser.get(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                        @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(currentUser.get(), id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(@PathVariable Long id,
                                              @Valid @RequestBody TaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(currentUser.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.deleteTask(currentUser.get(), id);
        return ResponseEntity.noContent().build();
    }
}