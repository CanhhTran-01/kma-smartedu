package KMA.SmartEdu.training.major;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    boolean existsByFacultyId(Long facultyId);
}
