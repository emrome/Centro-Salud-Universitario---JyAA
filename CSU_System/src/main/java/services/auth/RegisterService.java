package services.auth;

import dtos.auth.PublicRegistrationDTO;
import dtos.people.AdminCreateDTO;
import dtos.people.HealthStaffCreateDTO;
import dtos.people.SocialOrgRepresentativeCreateDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import services.people.AdminService;
import services.people.HealthStaffService;
import services.people.SocialOrgRepresentativeService;

import java.time.LocalDate;

@RequestScoped
public class RegisterService {

    @Inject HealthStaffService healthStaffService;
    @Inject SocialOrgRepresentativeService representativeService;

    public void register(PublicRegistrationDTO dto) {
        switch (dto.getUserType()) {
            case HealthStaff -> {
                HealthStaffCreateDTO hsDTO = new HealthStaffCreateDTO();
                hsDTO.setFirstName(dto.getFirstName());
                hsDTO.setLastName(dto.getLastName());
                hsDTO.setBirthDate(dto.getBirthDate());
                hsDTO.setEmail(dto.getEmail());
                hsDTO.setPassword(dto.getPassword());
                hsDTO.setEnabled(false);
                hsDTO.setRegistrationDate(LocalDate.now());
                hsDTO.setSpecialty(dto.getSpecialty());
                hsDTO.setLicense(dto.getLicense());

                healthStaffService.create(hsDTO);
            }

            case SocialOrgRepresentative -> {
                SocialOrgRepresentativeCreateDTO repDTO = new SocialOrgRepresentativeCreateDTO();
                repDTO.setFirstName(dto.getFirstName());
                repDTO.setLastName(dto.getLastName());
                repDTO.setBirthDate(dto.getBirthDate());
                repDTO.setEmail(dto.getEmail());
                repDTO.setPassword(dto.getPassword());
                repDTO.setEnabled(false);
                repDTO.setRegistrationDate(LocalDate.now());
                repDTO.setOrganizationId(dto.getOrganizationId());

                representativeService.create(repDTO);
            }

            default -> throw new IllegalArgumentException("Tipo de usuario inválido");
        }
    }
}


