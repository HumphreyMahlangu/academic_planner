package az.ac.mycput.repository;

import az.ac.mycput.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findByUserId(Long userId);
}