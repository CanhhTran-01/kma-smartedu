package KMA.SmartEdu.training.major.mapper;

import KMA.SmartEdu.training.major.Major;
import KMA.SmartEdu.training.major.dto.MajorRequest;
import KMA.SmartEdu.training.major.dto.MajorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MajorMapper {

    // map tay faculty ở service
    @Mapping(target = "faculty", ignore = true)
    Major toEntity(MajorRequest request);

    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(target = "facultyName", source = "faculty.facultyName")
    MajorResponse toResponse(Major major);

    // map tay faculty ở service, chỉ set các trường tương ứng, bỏ qua các trường không đồng nhất
    @Mapping(target = "faculty", ignore = true)
    void updateEntityFromRequest(MajorRequest request, @MappingTarget Major major);
}
