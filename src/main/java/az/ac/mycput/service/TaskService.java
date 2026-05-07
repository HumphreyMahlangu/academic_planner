package az.ac.mycput.service;

import az.ac.mycput.dto.AppDTO.*;
import az.ac.mycput.entity.Assessment;
import az.ac.mycput.entity.Module;
import az.ac.mycput.entity.Task;
import az.ac.mycput.entity.User;
import az.ac.mycput.repository.AssessmentRepository;
import az.ac.mycput.repository.ModuleRepository;
import az.ac.mycput.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ModuleRepository moduleRepository;
    private final AssessmentRepository assessmentRepository;

    public List<Task> getTasksForUser(User user, Long moduleId, Task.Status status) {
        Long userId = user.getId();

        if (moduleId != null && status != null) {
            return taskRepository.findByModuleSemesterUserIdAndModuleId(userId, moduleId)
                    .stream().filter(t -> t.getStatus() == status).toList();
        }
        if (moduleId != null) {
            return taskRepository.findByModuleSemesterUserIdAndModuleId(userId, moduleId);
        }
        if (status != null) {
            return taskRepository.findByModuleSemesterUserIdAndStatus(userId, status);
        }
        return taskRepository.findByModuleSemesterUserId(userId);
    }

    @Transactional
    public Task createTask(User user, TaskRequest request) {
        Module module = moduleRepository.findById(request.moduleId())
                .filter(m -> m.getSemester().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDeadline(request.deadline());
        task.setPriority(request.priority() != null ? request.priority() : Task.Priority.MEDIUM);
        task.setEstimatedHours(request.estimatedHours());
        task.setModule(module);

        if (request.assessmentId() != null) {
            Assessment assessment = assessmentRepository.findById(request.assessmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
            task.setAssessment(assessment);
        }

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateStatus(User user, Long taskId, TaskStatusRequest request) {
        Task task = getTaskForUser(user, taskId);
        task.setStatus(request.status());
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(User user, Long taskId, TaskRequest request) {
        Task task = getTaskForUser(user, taskId);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDeadline(request.deadline());
        task.setPriority(request.priority());
        task.setEstimatedHours(request.estimatedHours());
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(User user, Long taskId) {
        Task task = getTaskForUser(user, taskId);
        taskRepository.delete(task);
    }

    @Transactional
    public void markOverdueTasks() {
        List<Task> overdue = taskRepository.findAll().stream()
                .filter(t -> t.getStatus() == Task.Status.TODO || t.getStatus() == Task.Status.IN_PROGRESS)
                .filter(t -> t.getDeadline().isBefore(LocalDate.now()))
                .toList();

        overdue.forEach(t -> t.setStatus(Task.Status.OVERDUE));
        taskRepository.saveAll(overdue);
    }

    private Task getTaskForUser(User user, Long taskId) {
        return taskRepository.findById(taskId)
                .filter(t -> t.getModule().getSemester().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }
}