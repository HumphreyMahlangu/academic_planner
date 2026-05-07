package az.ac.mycput.repository;

import az.ac.mycput.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByModuleId(Long moduleId);

    List<Task> findByStatus(Task.Status status);

    List<Task> findByModuleSemesterUserId(Long userId);

    List<Task> findByModuleSemesterUserIdAndStatus(Long userId, Task.Status status);

    List<Task> findByModuleSemesterUserIdAndModuleId(Long userId, Long moduleId);

    @Query("SELECT t FROM Task t WHERE t.module.semester.user.id = :userId " +
           "AND t.deadline BETWEEN :from AND :to " +
           "AND t.status <> az.ac.mycput.entity.Task.Status.COMPLETED " +
           "ORDER BY t.deadline ASC, t.priority DESC")
    List<Task> findPendingTasksDueBetween(Long userId, LocalDate from, LocalDate to);

    @Query("SELECT t FROM Task t WHERE t.module.semester.user.id = :userId " +
           "AND t.deadline < :today " +
           "AND t.status <> az.ac.mycput.entity.Task.Status.COMPLETED")
    List<Task> findOverdueTasks(Long userId, LocalDate today);
}