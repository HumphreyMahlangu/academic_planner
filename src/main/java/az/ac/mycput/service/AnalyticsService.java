package az.ac.mycput.service;

import az.ac.mycput.entity.StudySession;
import az.ac.mycput.entity.Task;
import az.ac.mycput.repository.ModuleRepository;
import az.ac.mycput.repository.StudySessionRepository;
import az.ac.mycput.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TaskRepository taskRepository;
    private final StudySessionRepository studySessionRepository;
    private final ModuleRepository moduleRepository;

    public Map<String, Object> getOverview(Long userId) {
        Map<String, Object> overview = new LinkedHashMap<>();

        List<Task> allTasks = taskRepository.findByModuleSemesterUserId(userId);

        long total = allTasks.size();
        long completed = allTasks.stream().filter(t -> t.getStatus() == Task.Status.COMPLETED).count();
        long overdue = allTasks.stream().filter(t -> t.getStatus() == Task.Status.OVERDUE).count();
        long upcoming = allTasks.stream()
                .filter(t -> t.getStatus() == Task.Status.TODO || t.getStatus() == Task.Status.IN_PROGRESS)
                .filter(t -> t.getDeadline().isAfter(LocalDate.now()))
                .count();

        overview.put("totalTasks", total);
        overview.put("completedTasks", completed);
        overview.put("overdueTasks", overdue);
        overview.put("upcomingTasks", upcoming);
        overview.put("completionRate", total > 0 ? Math.round((double) completed / total * 100) : 0);

        List<StudySession> sessions = studySessionRepository.findByUserId(userId);

        Map<Long, Double> hoursPerModule = sessions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getModule().getId(),
                        Collectors.summingDouble(StudySession::getHoursStudied)
                ));

        Map<Long, Double> productivityPerModule = sessions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getModule().getId(),
                        Collectors.averagingDouble(StudySession::getProductivityRating)
                ));

        overview.put("studyHoursPerModule", hoursPerModule);
        overview.put("avgProductivityPerModule", productivityPerModule);
        overview.put("insights", buildInsights(allTasks, hoursPerModule));

        return overview;
    }

    private List<String> buildInsights(List<Task> tasks, Map<Long, Double> hoursPerModule) {
        List<String> insights = new ArrayList<>();

        long atRisk = tasks.stream()
                .filter(t -> t.getStatus() != Task.Status.COMPLETED)
                .filter(t -> {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), t.getDeadline());
                    return days >= 0 && days <= 3;
                })
                .count();

        if (atRisk > 0) {
            insights.add("You have " + atRisk + " task(s) due within the next 3 days.");
        }

        long overdue = tasks.stream().filter(t -> t.getStatus() == Task.Status.OVERDUE).count();
        if (overdue >= 3) {
            insights.add("You have " + overdue + " overdue tasks — consider rescheduling.");
        }

        tasks.stream()
                .filter(t -> t.getStatus() != Task.Status.COMPLETED)
                .map(t -> t.getModule().getId())
                .distinct()
                .forEach(moduleId -> {
                    double hours = hoursPerModule.getOrDefault(moduleId, 0.0);
                    if (hours == 0.0) {
                        moduleRepository.findById(moduleId).ifPresent(m ->
                                insights.add("No study time logged for " + m.getName() + " — it has open tasks."));
                    }
                });

        if (insights.isEmpty()) insights.add("You're on track — keep it up!");

        return insights;
    }
}