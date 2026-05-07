package az.ac.mycput.controller;

import az.ac.mycput.config.CurrentUser;
import az.ac.mycput.entity.Task;
import az.ac.mycput.service.ScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final CurrentUser currentUser;

    @GetMapping("/weekly")
    public ResponseEntity<Map<LocalDate, List<Task>>> getWeeklyView() {
        Long userId = currentUser.get().getId();
        return ResponseEntity.ok(scheduleService.getWeeklyView(userId));
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<LocalDate, List<Task>>> generate() {
        Long userId = currentUser.get().getId();
        return ResponseEntity.ok(scheduleService.generateWeeklySchedule(userId));
    }
}