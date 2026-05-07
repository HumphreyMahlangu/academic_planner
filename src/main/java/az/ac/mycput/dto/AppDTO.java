package az.ac.mycput.dto;

import az.ac.mycput.entity.Assessment;
import az.ac.mycput.entity.Task;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ── Auth ────

public class AppDTO {

    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String fullName
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(String token, String email, String fullName) {}

    // ── Semester ──────────────────────────────────────────────────────────────

    public record SemesterRequest(
            @NotBlank String name,
            String academicYear,
            LocalDate startDate,
            LocalDate endDate
    ) {}

    // ── Module ────────────────────────────────────────────

    public record ModuleRequest(
            @NotBlank 
            String name,
            String code,
            int credits,
            @NotNull 
            Long semesterId
    ) {}

    // ── Assessment ───────────────────────────────────────────

    public record AssessmentRequest(
            @NotBlank String title,
            @NotNull Assessment.Type type,
            int weightPercent,
            LocalDate dueDate,
            @NotNull Long moduleId
    ) {}

    public record MarkAssessmentRequest(
            @NotNull Double marksEarned,
            @NotNull Double marksTotal
    ) {}

    // ── Task ──────────────────────────────────────────────────────────────

    public record TaskRequest(
            @NotBlank String title,
            String description,
            @NotNull LocalDate deadline,
            Task.Priority priority,
            double estimatedHours,
            Long assessmentId,
            @NotNull Long moduleId
    ) {}

    public record TaskStatusRequest(@NotNull Task.Status status) {}

    // ── Study Session ─────────────────────────────────────────────────────────

    public record StudySessionRequest(
            @NotNull LocalDateTime startTime,
            @NotNull LocalDateTime endTime,
            @NotNull Long moduleId,
            int productivityRating,
            String notes
    ) {}
}