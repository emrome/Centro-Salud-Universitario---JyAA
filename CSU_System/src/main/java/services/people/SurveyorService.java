package services.people;

import daos.people.SurveyorDAO;
import dtos.people.SurveyorDTO;
import exceptions.DuplicateResourceException;
import exceptions.ResourceNotFoundException;
import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;
import mappers.people.SurveyorMapper;
import models.people.Surveyor;
import utils.TransactionHelper;

import java.util.List;

@RequestScoped
public class SurveyorService {

    @Inject
    SurveyorDAO surveyorDAO;
    @Inject
    TransactionHelper txHelper;
    @Inject
    private SurveyorMapper SurveyorMapper;

    public List<SurveyorDTO> getAll() {
        return SurveyorMapper.toDTOList(surveyorDAO.findAll());
    }

    public SurveyorDTO getById(Long id) {
        return SurveyorMapper.toDTO(validateSurveyorExists(id));
    }

    public SurveyorDTO create(SurveyorDTO dto) {
        if (surveyorDAO.findByDni(dto.getDni()).isPresent()) {
            throw new DuplicateResourceException("A surveyor with DNI " + dto.getDni() + " already exists.");
        }
        validateBirthDate(dto);
        Surveyor surveyor = SurveyorMapper.fromDTO(dto);
        surveyorDAO.save(surveyor);
        return SurveyorMapper.toDTO(surveyor);
    }

    public SurveyorDTO update(Long id, SurveyorDTO dto) {
        Surveyor surveyor = validateSurveyorExists(id);
        validateBirthDate(dto);

        String newDni = dto.getDni();
        String currentDni = surveyor.getDni();

        if (newDni != null && !newDni.equals(currentDni)) {
            surveyorDAO.findByDni(newDni).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("A surveyor with DNI " + newDni + " already exists.");
                }
            });
        }

        SurveyorMapper.updateFromDTO(dto, surveyor);
        surveyorDAO.update(surveyor);
        return SurveyorMapper.toDTO(surveyor);
    }

    public void delete(Long id) {
        txHelper.executeInTransaction(() -> {
            Surveyor surveyor = validateSurveyorExists(id);
            surveyorDAO.delete(surveyor);
        });
    }

    private Surveyor validateSurveyorExists(Long id) {
        Surveyor surveyor = surveyorDAO.findById(id);
        if (surveyor == null) {
            throw new ResourceNotFoundException("Surveyor not found");
        }
        return surveyor;
    }

    private void validateBirthDate(SurveyorDTO dto) {
        if (dto.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        if (dto.getBirthDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
    }
}

