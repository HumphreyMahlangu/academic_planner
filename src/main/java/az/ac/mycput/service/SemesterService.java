package az.ac.mycput.service;

import az.ac.mycput.dto.AppDTO.SemesterRequest;
import az.ac.mycput.entity.Semester;
import az.ac.mycput.entity.User;
import az.ac.mycput.repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterService {

    private final SemesterRepository semesterRepository;

    public List<Semester> getSemesters(User user) {
        return semesterRepository.findByUserId(user.getId());
    }

    @Transactional
    public Semester create(User user, SemesterRequest request) {
        Semester semester = new Semester();
        semester.setName(request.name());
        semester.setAcademicYear(request.academicYear());
        semester.setStartDate(request.startDate());
        semester.setEndDate(request.endDate());
        semester.setUser(user);
        return semesterRepository.save(semester);
    }

    @Transactional
    public Semester update(User user, Long id, SemesterRequest request) {
        Semester semester = getForUser(user, id);
        semester.setName(request.name());
        semester.setAcademicYear(request.academicYear());
        semester.setStartDate(request.startDate());
        semester.setEndDate(request.endDate());
        return semesterRepository.save(semester);
    }

    @Transactional
    public void delete(User user, Long id) {
        semesterRepository.delete(getForUser(user, id));
    }

    private Semester getForUser(User user, Long id) {
        return semesterRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Semester not found"));
    }
}