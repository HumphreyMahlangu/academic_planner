package az.ac.mycput.service;

import az.ac.mycput.dto.AppDTO.StudySessionRequest;
import az.ac.mycput.entity.Module;
import az.ac.mycput.entity.StudySession;
import az.ac.mycput.entity.User;
import az.ac.mycput.repository.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final ModuleService moduleService;

    public List<StudySession> getSessions(User user, Long moduleId) {
        if (moduleId != null) {
            return studySessionRepository.findByUserIdAndModuleId(user.getId(), moduleId);
        }
        return studySessionRepository.findByUserId(user.getId());
    }

    @Transactional
    public StudySession log(User user, StudySessionRequest request) {
        if (request.productivityRating() < 1 || request.productivityRating() > 5) {
            throw new IllegalArgumentException("Productivity rating must be between 1 and 5");
        }
        if (request.endTime().isBefore(request.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Module module = moduleService.getForUser(user, request.moduleId());

        StudySession session = new StudySession();
        session.setStartTime(request.startTime());
        session.setEndTime(request.endTime());
        session.setModule(module);
        session.setUser(user);
        session.setProductivityRating(request.productivityRating());
        session.setNotes(request.notes());
        return studySessionRepository.save(session);
    }

    @Transactional
    public void delete(User user, Long id) {
        StudySession session = studySessionRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
        studySessionRepository.delete(session);
    }
}