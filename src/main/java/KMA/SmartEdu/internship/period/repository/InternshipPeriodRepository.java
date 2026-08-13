package KMA.SmartEdu.internship.period.repository;

import KMA.SmartEdu.internship.period.entity.InternshipPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InternshipPeriodRepository
        extends JpaRepository<InternshipPeriod, Long>, JpaSpecificationExecutor<InternshipPeriod> {
    boolean existsByNameAndDeletedFalse(String name);

    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long id);
}
