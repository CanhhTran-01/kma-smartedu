package KMA.SmartEdu.training.major;

import KMA.SmartEdu.training.faculty.Faculty;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class MajorSpecification {

    public static Specification<Major> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null
                : cb.like(cb.lower(root.get("majorName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Major> hasCode(String code) {
        return (root, query, cb) -> code == null || code.isBlank()
                ? null
                : cb.like(cb.lower(root.get("majorCode")), "%" + code.toLowerCase() + "%");
    }

    public static Specification<Major> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("active"), active);
    }

    public static Specification<Major> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    public static Specification<Major> hasFacultyId(Long facultyId) {
        return (root, query, cb) ->
                facultyId == null ? null : cb.equal(root.get("faculty").get("id"), facultyId);
    }

    public static Specification<Major> hasFacultyName(String facultyName) {
        return (root, query, cb) -> {
            if (facultyName == null || facultyName.isBlank()) return null;

            // Explicit JOIN
            // LEFT JOIN - tránh bị giấu record: Major được duyệt tới chưa được gán vào Faculty nào
            Join<Major, Faculty> facultyJoin = root.join("faculty", JoinType.LEFT);

            return cb.like(cb.lower(facultyJoin.get("facultyName")), "%" + facultyName.toLowerCase() + "%");
        };
    }
}
