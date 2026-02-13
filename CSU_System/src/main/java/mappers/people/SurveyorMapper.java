package mappers.people;

import dtos.people.SurveyorDTO;
import jakarta.enterprise.context.ApplicationScoped;
import models.people.Surveyor;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SurveyorMapper {

    public SurveyorDTO toDTO(Surveyor surveyor) {
        if (surveyor == null) return null;

        SurveyorDTO dto = new SurveyorDTO();
        dto.setId(surveyor.getId());
        dto.setFirstName(surveyor.getFirstName());
        dto.setLastName(surveyor.getLastName());
        dto.setBirthDate(surveyor.getBirthDate());
        dto.setDni(surveyor.getDni());
        dto.setGender(surveyor.getGender());
        dto.setOccupation(surveyor.getOccupation());
        return dto;
    }

    public Surveyor fromDTO(SurveyorDTO dto) {
        if (dto == null) return null;

        Surveyor surveyor = new Surveyor();
        surveyor.setFirstName(dto.getFirstName());
        surveyor.setLastName(dto.getLastName());
        surveyor.setBirthDate(dto.getBirthDate());
        surveyor.setDni(dto.getDni());
        surveyor.setGender(dto.getGender());
        surveyor.setOccupation(dto.getOccupation());
        return surveyor;
    }

    public List<SurveyorDTO> toDTOList(List<Surveyor> surveyors) {
        if (surveyors == null) return null;
        return surveyors.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateFromDTO(SurveyorDTO dto, Surveyor existing) {
        if (dto == null || existing == null) return;

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setBirthDate(dto.getBirthDate());
        existing.setDni(dto.getDni());
        existing.setGender(dto.getGender());
        existing.setOccupation(dto.getOccupation());
    }
}