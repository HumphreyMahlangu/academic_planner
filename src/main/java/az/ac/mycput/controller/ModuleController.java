package az.ac.mycput.controller;

import az.ac.mycput.config.CurrentUser;
import az.ac.mycput.dto.AppDTO.ModuleRequest;
import az.ac.mycput.entity.Module;
import az.ac.mycput.service.ModuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ModuleController {

    private final ModuleService moduleService;
    private final CurrentUser currentUser;

    @GetMapping
    public List<Module> getAll(@RequestParam(required = false) Long semesterId) {
        return moduleService.getModules(currentUser.get(), semesterId);
    }

    @PostMapping
    public ResponseEntity<Module> create(@Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(moduleService.create(currentUser.get(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Module> update(@PathVariable Long id,
                                          @Valid @RequestBody ModuleRequest request) {
        return ResponseEntity.ok(moduleService.update(currentUser.get(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moduleService.delete(currentUser.get(), id);
        return ResponseEntity.noContent().build();
    }
}