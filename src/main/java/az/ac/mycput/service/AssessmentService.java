package az.ac.mycput.service;

import az.ac.mycput.dto.AppDTO.AssessmentRequest;
import az.ac.mycput.dto.AppDTO.MarkAssessmentRequest;
import az.ac.mycput.entity.Assessment;
import az.ac.mycput.entity.Module;
import az.ac.mycput.entity.User;
import az.ac.mycput.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final ModuleService moduleService;

    public List<Assessment> getAssessments(Long moduleId) {
        return assessmentRepository.findByModuleId(moduleId);
    }

    @Transactional
    public Assessment create(User user, AssessmentRequest request) {
        Module module = moduleService.getForUser(user, request.moduleId());

        Assessment assessment = new Assessment();
        assessment.setTitle(request.title());
        assessment.setType(request.type());
        assessment.setWeightPercent(request.weightPercent());
        assessment.setDueDate(request.dueDate());
        assessment.setModule(module);
        return assessmentRepository.save(assessment);
    }

    @Transactional
    public Assessment markAssessment(User user, Long id, MarkAssessmentRequest request) {
        Assessment assessment = getForUser(user, id);
        assessment.setMarksEarned(request.marksEarned());
        assessment.setMarksTotal(request.marksTotal());
        Assessment saved = assessmentRepository.save(assessment);

        recalculateModuleAverage(assessment.getModule());
        return saved;
    }

    @Transactional
    public void delete(User user, Long id) {
        assessmentRepository.delete(getForUser(user, id));
    }

    private void recalculateModuleAverage(Module module) {
        List<Assessment> marked = assessmentRepository.findByModuleId(module.getId())
                .stream()
                .filter(a -> a.getMarksEarned() != null && a.getMarksTotal() != null && a.getMarksTotal() > 0)
                .toList();

        if (marked.isEmpty()) {
            module.setCurrentAverage(null);
            return;
        }

        double totalWeight = marked.stream().mapToInt(Assessment::getWeightPercent).sum();
        double weightedSum = marked.stream()
                .mapToDouble(a -> (a.getMarksEarned() / a.getMarksTotal() * 100.0) * a.getWeightPercent())
                .sum();

        module.setCurrentAverage(totalWeight > 0 ? weightedSum / totalWeight : null);
    }

    private Assessment getForUser(User user, Long id) {
        return assessmentRepository.findById(id)
                .filter(a -> a.getModule().getSemester().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found"));
    }
}