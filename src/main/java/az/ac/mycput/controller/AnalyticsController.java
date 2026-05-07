package az.ac.mycput.controller;

import az.ac.mycput.config.CurrentUser;
import az.ac.mycput.service.AnalyticsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CurrentUser currentUser;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> overview() {
        return ResponseEntity.ok(analyticsService.getOverview(currentUser.get().getId()));
    }
}