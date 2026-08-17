package KMA.SmartEdu.internship.period.specification;

import KMA.SmartEdu.internship.period.entity.InternshipPeriod;
import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import org.springframework.data.jpa.domain.Specification;

public class InternshipPeriodSpecification {

    public static Specification<InternshipPeriod> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<InternshipPeriod> hasInternshipType(InternshipTypeEnum internshipType) {
        return (root, query, cb) ->
                internshipType == null ? null : cb.equal(root.get("internshipType"), internshipType);
    }

    public static Specification<InternshipPeriod> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }
}
