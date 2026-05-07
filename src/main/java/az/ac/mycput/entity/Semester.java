package az.ac.mycput.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "semesters")
@Data
@NoArgsConstructor
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String academicYear;

    private LocalDate startDate;
    private LocalDate endDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();
}