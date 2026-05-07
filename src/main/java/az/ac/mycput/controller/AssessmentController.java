package az.ac.mycput.controller;


import az.ac.mycput.config.CurrentUser;
import az.ac.mycput.dto.AppDTO.AssessmentRequest;
import az.ac.mycput.dto.AppDTO.MarkAssessmentRequest;
import az.ac.mycput.entity.Assessment;
import az.ac.mycput.service.AssessmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AssessmentController {

    private final AssessmentService assessmentService;
    private final CurrentUser currentUser;

    @GetMapping
    public List<Assessment> getByModule(@RequestParam Long moduleId) {
        return assessmentService.getAssessments(moduleId);
    }

    @PostMapping
    public ResponseEntity<Assessment> create(@Valid @RequestBody AssessmentRequest request) {
        return ResponseEntity.ok(assessmentService.create(currentUser.get(), request));
    }

    @PatchMapping("/{id}/mark")
    public ResponseEntity<Assessment> mark(@PathVariable Long id,
                                            @Valid @RequestBody MarkAssessmentRequest request) {
        return ResponseEntity.ok(assessmentService.markAssessment(currentUser.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assessmentService.delete(currentUser.get(), id);
        return ResponseEntity.noContent().build();
    }
}