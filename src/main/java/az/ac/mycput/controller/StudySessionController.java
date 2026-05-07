package az.ac.mycput.controller;

import az.ac.mycput.config.CurrentUser;
import az.ac.mycput.dto.AppDTO.StudySessionRequest;
import az.ac.mycput.entity.StudySession;
import az.ac.mycput.service.StudySessionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-sessions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class StudySessionController {

    private final StudySessionService studySessionService;
    private final CurrentUser currentUser;

    @GetMapping
    public List<StudySession> getAll(@RequestParam(required = false) Long moduleId) {
        return studySessionService.getSessions(currentUser.get(), moduleId);
    }

    @PostMapping
    public ResponseEntity<StudySession> log(@Valid @RequestBody StudySessionRequest request) {
        return ResponseEntity.ok(studySessionService.log(currentUser.get(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studySessionService.delete(currentUser.get(), id);
        return ResponseEntity.noContent().build();
    }
}