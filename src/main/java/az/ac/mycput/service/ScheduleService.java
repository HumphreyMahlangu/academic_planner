package az.ac.mycput.service;

import az.ac.mycput.entity.Task;
import az.ac.mycput.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final TaskRepository taskRepository;

    private static final double MAX_HOURS_PER_DAY = 6.0;
    private static final int SCHEDULE_AHEAD_DAYS = 14;


//     Generates a weekly schedule for the user.
//     Returns a map of date → list of tasks scheduled for that day.
//
//     Algorithm:
//     1. Fetch all pending tasks due within the next 2 weeks
//     2. Score each task: higher score = schedule sooner
//        Score = (1/daysUntilDeadline) * priorityWeight
//     3. Distribute tasks across days respecting MAX_HOURS_PER_DAY
//     4. Persist the scheduledDate on each task

    @Transactional
    public Map<LocalDate, List<Task>> generateWeeklySchedule(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate horizon = today.plusDays(SCHEDULE_AHEAD_DAYS);

        List<Task> pending = taskRepository.findPendingTasksDueBetween(userId, today, horizon);

        pending.sort(Comparator.comparingDouble(this::score).reversed());

        Map<LocalDate, Double> hoursUsed = new LinkedHashMap<>();
        for (int i = 0; i <= SCHEDULE_AHEAD_DAYS; i++) {
            hoursUsed.put(today.plusDays(i), 0.0);
        }

        Map<LocalDate, List<Task>> schedule = new LinkedHashMap<>();
        hoursUsed.forEach((date, _) -> schedule.put(date, new ArrayList<>()));

        for (Task task : pending) {
            LocalDate cap = task.getDeadline().isBefore(horizon) ? task.getDeadline() : horizon;

            Optional<LocalDate> slot = findSlot(hoursUsed, today, cap, task.getEstimatedHours());
            slot.ifPresent(date -> {
                schedule.get(date).add(task);
                hoursUsed.merge(date, task.getEstimatedHours(), Double::sum);
                task.setScheduledDate(date);
                taskRepository.save(task);
            });
        }

        return schedule;
    }

    public Map<LocalDate, List<Task>> getWeeklyView(Long userId) {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        List<Task> tasks = taskRepository.findPendingTasksDueBetween(userId, monday, sunday);

        Map<LocalDate, List<Task>> view = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) view.put(monday.plusDays(i), new ArrayList<>());

        for (Task t : tasks) {
            if (t.getScheduledDate() != null && view.containsKey(t.getScheduledDate())) {
                view.get(t.getScheduledDate()).add(t);
            }
        }
        return view;
    }

    // Helpers

    private double score(Task task) {
        long daysUntilDeadline = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), task.getDeadline());
        if (daysUntilDeadline <= 0) daysUntilDeadline = 1;

        double priorityWeight = switch (task.getPriority()) {
            case HIGH -> 3.0;
            case MEDIUM -> 2.0;
            case LOW -> 1.0;
        };

        return (1.0 / daysUntilDeadline) * priorityWeight;
    }

    private Optional<LocalDate> findSlot(Map<LocalDate, Double> hoursUsed,
                                          LocalDate from, LocalDate cap,
                                          double neededHours) {
        LocalDate date = from;
        while (!date.isAfter(cap)) {
            double used = hoursUsed.getOrDefault(date, 0.0);
            if (used + neededHours <= MAX_HOURS_PER_DAY) {
                return Optional.of(date);
            }
            date = date.plusDays(1);
        }
        return Optional.empty();
    }
}