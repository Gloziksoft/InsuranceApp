package com.insurance.app.models.dto.mappers;

import com.insurance.app.data.entities.InsuranceEntity;
import com.insurance.app.models.dto.InsuranceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between InsuranceEntity and InsuranceDTO.
 */
@Mapper(componentModel = "spring")
public interface InsuranceMapper {

    /**
     * Maps InsuranceDTO to InsuranceEntity.
     * Ignores setting insuredPerson and policyHolder (handled separately).
     */
    @Mapping(target = "insuredPerson", ignore = true)
    @Mapping(target = "policyHolder", ignore = true)
    InsuranceEntity toEntity(InsuranceDTO dto);

    /**
     * Maps InsuranceEntity to InsuranceDTO.
     * Extracts IDs and names from insuredPerson and policyHolder.
     */
    @Mapping(source = "insuredPerson.id", target = "insuredPersonId")
    @Mapping(source = "policyHolder.id", target = "policyHolderId")
    @Mapping(source = "insuredPerson.firstName", target = "insuredPersonFirstName")
    @Mapping(source = "insuredPerson.lastName", target = "insuredPersonLastName")
    @Mapping(source = "policyHolder.firstName", target = "policyHolderFirstName")
    @Mapping(source = "policyHolder.lastName", target = "policyHolderLastName")
    InsuranceDTO toDTO(InsuranceEntity entity);
}
