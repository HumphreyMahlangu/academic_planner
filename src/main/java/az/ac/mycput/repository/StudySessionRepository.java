package az.ac.mycput.repository;

import az.ac.mycput.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByUserId(Long userId);

    List<StudySession> findByUserIdAndModuleId(Long userId, Long moduleId);
}