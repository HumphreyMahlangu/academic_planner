package az.ac.mycput.controller;

import az.ac.mycput.config.CurrentUser;
import az.ac.mycput.dto.AppDTO.SemesterRequest;
import az.ac.mycput.entity.Semester;
import az.ac.mycput.service.SemesterService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SemesterController {

    private final SemesterService semesterService;
    private final CurrentUser currentUser;

    @GetMapping
    public List<Semester> getAll() {
        return semesterService.getSemesters(currentUser.get());
    }

    @PostMapping
    public ResponseEntity<Semester> create(@Valid @RequestBody SemesterRequest request) {
        return ResponseEntity.ok(semesterService.create(currentUser.get(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Semester> update(@PathVariable Long id,
                                            @Valid @RequestBody SemesterRequest request) {
        return ResponseEntity.ok(semesterService.update(currentUser.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        semesterService.delete(currentUser.get(), id);
        return ResponseEntity.noContent().build();
    }
}