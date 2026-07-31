package KMA.SmartEdu.training.faculty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long>, JpaSpecificationExecutor<Faculty> {
    boolean existsByFacultyCodeAndIdNot(String facultyCode, Long id);

    boolean existsByFacultyCode(String facultyCode);
}
