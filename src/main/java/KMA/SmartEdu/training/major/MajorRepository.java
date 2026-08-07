package KMA.SmartEdu.training.major;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long>, JpaSpecificationExecutor<Major> {
    boolean existsByMajorCode(String majorCode);

    boolean existsByMajorCodeAndIdNot(String majorCode, Long id);

    boolean existsByFacultyId(Long facultyId);
}
