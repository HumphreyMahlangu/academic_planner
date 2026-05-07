package az.ac.mycput.repository;

import az.ac.mycput.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findBySemesterId(Long semesterId);
    List<Module> findBySemesterUserId(Long userId);
}