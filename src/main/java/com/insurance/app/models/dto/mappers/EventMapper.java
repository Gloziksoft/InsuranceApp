package com.insurance.app.models.dto.mappers;

import com.insurance.app.data.entities.EventEntity;
import com.insurance.app.models.dto.EventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between EventEntity and EventDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    /**
     * Maps EventEntity to EventDTO.
     * Maps insurance ID from related insurance entity.
     */
    @Mapping(source = "insurance.id", target = "insuranceId")
    EventDTO toDto(EventEntity entity);

    /**
     * Maps EventDTO to EventEntity.
     * Maps insurance ID into insurance reference.
     */
    @Mapping(source = "insuranceId", target = "insurance.id")
    EventEntity toEntity(EventDTO dto);
}
