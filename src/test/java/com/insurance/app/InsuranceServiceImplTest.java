package com.insurance.app;

import com.insurance.app.data.entities.InsuranceEntity;
import com.insurance.app.data.entities.InsuredPersonEntity;
import com.insurance.app.data.repositories.InsuranceRepository;
import com.insurance.app.data.repositories.InsuredPersonRepository;
import com.insurance.app.models.dto.InsuranceDTO;
import com.insurance.app.models.dto.mappers.InsuranceMapper;
import com.insurance.app.models.services.EventService;
import com.insurance.app.models.services.InsuranceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsuranceServiceImplTest {

    @Mock
    private InsuranceRepository repository;

    @Mock
    private InsuranceMapper mapper;

    @Mock
    private InsuredPersonRepository insuredPersonRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private InsuranceServiceImpl insuranceService;


    @Test
    void findById_shouldReturnInsurance_whenInsuranceExists() {

        // Arrange
        Long id = 1L;

        InsuranceEntity entity = mock(InsuranceEntity.class);
        InsuranceDTO dto = mock(InsuranceDTO.class);

        when(repository.findByIdWithRelations(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toDTO(entity))
                .thenReturn(dto);

        // Act
        InsuranceDTO result = insuranceService.findById(id);

        // Assert
        assertNotNull(result);
        assertSame(dto, result);

        verify(repository).findByIdWithRelations(id);
        verify(mapper).toDTO(entity);
    }


    @Test
    void findById_shouldThrowException_whenInsuranceDoesNotExist() {

        // Arrange
        Long id = 999L;

        when(repository.findByIdWithRelations(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> insuranceService.findById(id)
        );

        assertEquals(
                "Insurance with ID 999 was not found.",
                exception.getMessage()
        );

        verify(repository).findByIdWithRelations(id);
        verify(mapper, never()).toDTO(any());
    }
    @Test
    void create_shouldCreateInsurance_whenInsuredPersonExists() {

        // Arrange
        InsuranceDTO dto = mock(InsuranceDTO.class);
        InsuranceEntity entity = mock(InsuranceEntity.class);
        InsuranceEntity savedEntity = mock(InsuranceEntity.class);
        InsuredPersonEntity insuredPerson = mock(InsuredPersonEntity.class);

        when(dto.getInsuredPersonId()).thenReturn(1L);
        when(dto.getPolicyHolderId()).thenReturn(null);

        when(mapper.toEntity(dto))
                .thenReturn(entity);

        when(insuredPersonRepository.findById(1L))
                .thenReturn(Optional.of(insuredPerson));

        when(repository.save(entity))
                .thenReturn(savedEntity);

        when(savedEntity.getInsuredPerson())
                .thenReturn(insuredPerson);

        when(mapper.toDTO(savedEntity))
                .thenReturn(dto);

        // Act
        InsuranceDTO result = insuranceService.create(dto);

        // Assert
        assertNotNull(result);
        assertSame(dto, result);

        verify(mapper).toEntity(dto);
        verify(insuredPersonRepository).findById(1L);
        verify(entity).setInsuredPerson(insuredPerson);
        verify(repository).save(entity);
        verify(eventService).save(any());

        verify(mapper).toDTO(savedEntity);
    }
    @Test
    void create_shouldThrowException_whenInsuredPersonDoesNotExist() {

        // Arrange
        InsuranceDTO dto = mock(InsuranceDTO.class);
        InsuranceEntity entity = mock(InsuranceEntity.class);

        when(dto.getInsuredPersonId()).thenReturn(999L);

        when(mapper.toEntity(dto))
                .thenReturn(entity);

        when(insuredPersonRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> insuranceService.create(dto)
        );

        assertEquals(
                "Insured person does not exist.",
                exception.getMessage()
        );

        verify(mapper).toEntity(dto);
        verify(insuredPersonRepository).findById(999L);

        verify(repository, never()).save(any());
        verify(eventService, never()).save(any());
    }

    @Test
    void delete_shouldDeleteInsurance_whenInsuranceExists() {

        // Arrange
        Long id = 1L;

        InsuranceEntity insurance = mock(InsuranceEntity.class);
        InsuredPersonEntity insuredPerson = mock(InsuredPersonEntity.class);

        when(repository.findById(id))
                .thenReturn(Optional.of(insurance));

        when(insurance.getInsuredPerson())
                .thenReturn(insuredPerson);

        when(insuredPerson.getFirstName())
                .thenReturn("Peter");

        when(insuredPerson.getLastName())
                .thenReturn("Test");

        when(insurance.getId())
                .thenReturn(id);

        // Act
        insuranceService.delete(id);

        // Assert
        verify(repository).findById(id);
        verify(eventService).save(any());
        verify(repository).deleteById(id);
    }

    @Test
    void update_shouldUpdateInsurance_whenInsuranceExists() {

        // Arrange
        Long id = 1L;

        InsuranceDTO dto = mock(InsuranceDTO.class);
        InsuranceEntity insurance = mock(InsuranceEntity.class);
        InsuranceEntity savedInsurance = mock(InsuranceEntity.class);
        InsuredPersonEntity insuredPerson = mock(InsuredPersonEntity.class);

        when(repository.findById(id))
                .thenReturn(Optional.of(insurance));

        when(dto.getInsuredPersonId())
                .thenReturn(1L);

        when(dto.getPolicyHolderId())
                .thenReturn(null);

        when(insuredPersonRepository.findById(1L))
                .thenReturn(Optional.of(insuredPerson));

        when(repository.save(insurance))
                .thenReturn(savedInsurance);

        when(mapper.toDTO(savedInsurance))
                .thenReturn(dto);

        when(savedInsurance.getInsuredPerson())
                .thenReturn(insuredPerson);

        when(savedInsurance.getId())
                .thenReturn(id);

        when(insuredPerson.getFirstName())
                .thenReturn("Peter");

        when(insuredPerson.getLastName())
                .thenReturn("Test");


        // Act
        InsuranceDTO result = insuranceService.update(id, dto);


        // Assert
        assertNotNull(result);
        assertSame(dto, result);

        verify(repository).findById(id);
        verify(insuredPersonRepository).findById(1L);
        verify(insurance).setInsuredPerson(insuredPerson);
        verify(insurance).setPolicyHolder(null);
        verify(repository).save(insurance);
        verify(eventService).save(any());
        verify(mapper).toDTO(savedInsurance);
    }

    @Test
    void update_shouldThrowException_whenInsuranceDoesNotExist() {

        // Arrange
        Long id = 999L;

        InsuranceDTO dto = mock(InsuranceDTO.class);

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> insuranceService.update(id, dto)
        );

        assertEquals(
                "Insurance with ID 999 was not found.",
                exception.getMessage()
        );

        // Overíme, že sa ďalej už nič nevykonalo
        verify(repository).findById(id);
        verify(repository, never()).save(any());
        verify(insuredPersonRepository, never()).findById(anyLong());
        verify(eventService, never()).save(any());
        verify(mapper, never()).toDTO(any());
    }

    @Test
    void findAll_shouldReturnInsurancePage() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        InsuranceEntity entity1 = mock(InsuranceEntity.class);
        InsuranceEntity entity2 = mock(InsuranceEntity.class);

        InsuranceDTO dto1 = mock(InsuranceDTO.class);
        InsuranceDTO dto2 = mock(InsuranceDTO.class);

        Page<InsuranceEntity> entityPage =
                new PageImpl<>(List.of(entity1, entity2), pageable, 2);

        when(repository.findAll(pageable))
                .thenReturn(entityPage);

        when(mapper.toDTO(entity1))
                .thenReturn(dto1);

        when(mapper.toDTO(entity2))
                .thenReturn(dto2);

        // Act
        Page<InsuranceDTO> result =
                insuranceService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        assertSame(dto1, result.getContent().get(0));
        assertSame(dto2, result.getContent().get(1));

        verify(repository).findAll(pageable);
        verify(mapper).toDTO(entity1);
        verify(mapper).toDTO(entity2);
    }

    @Test
    void search_shouldReturnMatchingInsurances() {

        // Arrange
        String keyword = "Peter";
        Pageable pageable = PageRequest.of(0, 10);

        InsuranceEntity entity1 = mock(InsuranceEntity.class);
        InsuranceEntity entity2 = mock(InsuranceEntity.class);

        InsuranceDTO dto1 = mock(InsuranceDTO.class);
        InsuranceDTO dto2 = mock(InsuranceDTO.class);

        Page<InsuranceEntity> entityPage =
                new PageImpl<>(List.of(entity1, entity2), pageable, 2);

        when(repository
                .findByInsuredPerson_FirstNameContainingIgnoreCaseOrInsuredPerson_LastNameContainingIgnoreCase(
                        keyword, keyword, pageable))
                .thenReturn(entityPage);

        when(mapper.toDTO(entity1))
                .thenReturn(dto1);

        when(mapper.toDTO(entity2))
                .thenReturn(dto2);

        // Act
        Page<InsuranceDTO> result =
                insuranceService.search(keyword, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());

        assertSame(dto1, result.getContent().get(0));
        assertSame(dto2, result.getContent().get(1));

        verify(repository)
                .findByInsuredPerson_FirstNameContainingIgnoreCaseOrInsuredPerson_LastNameContainingIgnoreCase(
                        keyword, keyword, pageable);

        verify(mapper).toDTO(entity1);
        verify(mapper).toDTO(entity2);
    }
}