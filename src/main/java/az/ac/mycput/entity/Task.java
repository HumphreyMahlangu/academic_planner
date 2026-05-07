package az.ac.mycput.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    private double estimatedHours;

    @Enumerated(EnumType.STRING)
    private Status status = Status.TODO;

    private LocalDate scheduledDate;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        TODO, IN_PROGRESS, COMPLETED, OVERDUE
    }
}