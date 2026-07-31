package KMA.SmartEdu.training.faculty.mapper;

import KMA.SmartEdu.training.faculty.Faculty;
import KMA.SmartEdu.training.faculty.dto.FacultyRequest;
import KMA.SmartEdu.training.faculty.dto.FacultyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FacultyMapper {
    Faculty toEntity(FacultyRequest request);

    FacultyResponse toResponse(Faculty faculty);

    void updateEntityFromRequest(FacultyRequest request, @MappingTarget Faculty faculty);
}
