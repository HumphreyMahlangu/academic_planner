package az.ac.mycput.repository;

import az.ac.mycput.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByModuleId(Long moduleId);
}