package az.ac.mycput.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "modules")
@Data
@NoArgsConstructor
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String code;

    private int credits;

    private Double currentAverage;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @JsonIgnore
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assessment> assessments = new ArrayList<>();
}