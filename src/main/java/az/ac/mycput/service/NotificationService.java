package az.ac.mycput.service;

import az.ac.mycput.entity.Task;
import az.ac.mycput.entity.User;
import az.ac.mycput.repository.TaskRepository;
import az.ac.mycput.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendDeadlineReminders() {
        List<User> users = userRepository.findAll();
        users.forEach(this::sendRemindersForUser);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void markOverdueTasks() {
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(t -> t.getStatus() == Task.Status.TODO || t.getStatus() == Task.Status.IN_PROGRESS)
                .filter(t -> t.getDeadline().isBefore(LocalDate.now()))
                .toList();

        tasks.forEach(t -> t.setStatus(Task.Status.OVERDUE));
        taskRepository.saveAll(tasks);
        log.info("Marked {} tasks as overdue", tasks.size());
    }

    private void sendRemindersForUser(User user) {
        LocalDate today = LocalDate.now();

        int[] reminderDays = {1, 3, 7};
        for (int days : reminderDays) {
            LocalDate targetDate = today.plusDays(days);
            List<Task> dueSoon = taskRepository
                    .findPendingTasksDueBetween(user.getId(), targetDate, targetDate);

            if (!dueSoon.isEmpty()) {
                sendEmail(user.getEmail(), days, dueSoon);
            }
        }
    }

    private void sendEmail(String email, int daysAway, List<Task> tasks) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Academic Planner — " + daysAway + " day reminder");

            StringBuilder body = new StringBuilder();
            body.append("You have tasks due in ").append(daysAway).append(" day(s):\n\n");
            tasks.forEach(t -> body.append("• ").append(t.getTitle())
                    .append(" (").append(t.getModule().getName()).append(")")
                    .append(" — due ").append(t.getDeadline()).append("\n"));

            message.setText(body.toString());
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send reminder email to {}: {}", email, e.getMessage());
        }
    }
}