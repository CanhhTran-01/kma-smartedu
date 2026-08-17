package KMA.SmartEdu.training.faculty;

import org.springframework.data.jpa.domain.Specification;

public class FacultySpecification {

    public static Specification<Faculty> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null
                : cb.like(cb.lower(root.get("facultyName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Faculty> hasCode(String code) {
        return (root, query, cb) -> code == null || code.isBlank()
                ? null
                : cb.like(cb.lower(root.get("facultyCode")), "%" + code.toLowerCase() + "%");
    }

    public static Specification<Faculty> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<Faculty> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }
}
