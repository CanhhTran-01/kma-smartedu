package KMA.SmartEdu.internship.period.mapper;

import KMA.SmartEdu.internship.period.dto.InternshipPeriodRequest;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodResponse;
import KMA.SmartEdu.internship.period.entity.InternshipPeriod;
import KMA.SmartEdu.internship.period.enums.PeriodStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InternshipPeriodMapper {

    InternshipPeriod toEntity(InternshipPeriodRequest request);

    @Mapping(target = "status", ignore = true)
    InternshipPeriodResponse toResponse(InternshipPeriod internshipPeriod);

    void updateEntityFromRequest(InternshipPeriodRequest request, @MappingTarget InternshipPeriod internshipPeriod);

    @AfterMapping
    default void fillStatus(
            @MappingTarget InternshipPeriodResponse.InternshipPeriodResponseBuilder response,
            InternshipPeriod internshipPeriod) {
        response.status(PeriodStatus.of(
                internshipPeriod.getStartDate(),
                internshipPeriod.getEndDate(),
                internshipPeriod.getRegistrationDeadline()));
    }
}
