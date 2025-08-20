package com.insurance.app.models.dto.mappers;

import com.insurance.app.data.entities.InsuranceEntity;
import com.insurance.app.data.entities.InsuredPersonEntity;
import com.insurance.app.models.dto.InsuranceDTO;
import com.insurance.app.models.dto.InsuredPersonDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper for converting between InsuredPersonEntity, InsuranceEntity and their DTOs.
 */
@Mapper(componentModel = "spring")
public interface InsuredPersonMapper {

    /**
     * Maps InsuredPersonEntity to InsuredPersonDTO.
     */
    InsuredPersonDTO toDTO(InsuredPersonEntity entity);

    /**
     * Maps InsuranceEntity to InsuranceDTO.
     * Extracts fields from insuredPerson and policyHolder entities.
     */
    @Mapping(target = "insuredPersonFirstName", source = "insuredPerson.firstName")
    @Mapping(target = "insuredPersonLastName", source = "insuredPerson.lastName")
    @Mapping(target = "policyHolderFirstName", source = "policyHolder.firstName")
    @Mapping(target = "policyHolderLastName", source = "policyHolder.lastName")
    @Mapping(target = "insuredPersonId", source = "insuredPerson.id")
    @Mapping(target = "policyHolderId", source = "policyHolder.id")
    InsuranceDTO toDTO(InsuranceEntity entity);

    /**
     * Maps InsuranceDTO to InsuranceEntity.
     * Ignores setting insuredPerson and policyHolder (handled separately).
     */
    @Mapping(target = "insuredPerson", ignore = true)
    @Mapping(target = "policyHolder", ignore = true)
    InsuranceEntity toEntity(InsuranceDTO dto);

    /**
     * Maps InsuredPersonDTO to InsuredPersonEntity.
     */
    @Mapping(target = "id", ignore = true)
    InsuredPersonEntity toEntity(InsuredPersonDTO dto);

    /**
     * Updates an existing InsuredPersonDTO from InsuredPersonEntity.
     */
    void updateInsuredPersonDTO(InsuredPersonEntity source, @MappingTarget InsuredPersonDTO target);

    /**
     * Updates an existing InsuredPersonEntity from InsuredPersonDTO.
     */
    void updateInsuredPersonEntity(InsuredPersonDTO source, @MappingTarget InsuredPersonEntity target);
}
