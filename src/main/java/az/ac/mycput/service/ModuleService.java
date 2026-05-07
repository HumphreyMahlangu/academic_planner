package az.ac.mycput.service;

import az.ac.mycput.dto.AppDTO.ModuleRequest;
import az.ac.mycput.entity.Module;
import az.ac.mycput.entity.Semester;
import az.ac.mycput.entity.User;
import az.ac.mycput.repository.ModuleRepository;
import az.ac.mycput.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final SemesterRepository semesterRepository;

    public List<Module> getModules(User user, Long semesterId) {
        if (semesterId != null) {
            return moduleRepository.findBySemesterId(semesterId);
        }
        return moduleRepository.findBySemesterUserId(user.getId());
    }

    @Transactional
    public Module create(User user, ModuleRequest request) {
        Semester semester = semesterRepository.findById(request.semesterId())
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Semester not found"));

        Module module = new Module();
        module.setName(request.name());
        module.setCode(request.code());
        module.setCredits(request.credits());
        module.setSemester(semester);
        return moduleRepository.save(module);
    }

    @Transactional
    public Module update(User user, Long id, ModuleRequest request) {
        Module module = getForUser(user, id);
        module.setName(request.name());
        module.setCode(request.code());
        module.setCredits(request.credits());
        return moduleRepository.save(module);
    }

    @Transactional
    public void delete(User user, Long id) {
        moduleRepository.delete(getForUser(user, id));
    }

    public Module getForUser(User user, Long id) {
        return moduleRepository.findById(id)
                .filter(m -> m.getSemester().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Module not found"));
    }
}