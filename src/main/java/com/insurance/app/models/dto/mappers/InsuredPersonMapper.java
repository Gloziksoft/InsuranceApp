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
     * Maps InsuredPersonEntity to InsuredPersonDTO (bez poistiek - na listing).
     */
    @Mapping(target = "insurances", ignore = true) // 🚀 zabráni LazyInitializationException
    InsuredPersonDTO toDTO(InsuredPersonEntity entity);

    /**
     * Maps InsuredPersonEntity to InsuredPersonDTO (s poistkami - na detail).
     */
    InsuredPersonDTO toDTOWithInsurances(InsuredPersonEntity entity);

    /**
     * Maps InsuranceEntity to InsuranceDTO.
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
     */
    @Mapping(target = "insuredPerson", ignore = true)
    @Mapping(target = "policyHolder", ignore = true)
    InsuranceEntity toEntity(InsuranceDTO dto);

    /**
     * Maps InsuredPersonDTO to InsuredPersonEntity.
     */
    @Mapping(target = "id", ignore = true)
    InsuredPersonEntity toEntity(InsuredPersonDTO dto);

    void updateInsuredPersonDTO(InsuredPersonEntity source, @MappingTarget InsuredPersonDTO target);

    void updateInsuredPersonEntity(InsuredPersonDTO source, @MappingTarget InsuredPersonEntity target);
}

