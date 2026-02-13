package mappers;

import dtos.CoordinateDTO;
import dtos.NeighborhoodDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.Coordinate;
import models.Neighborhood;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class NeighborhoodMapper {

    @Inject
    private CoordinateMapper coordinateMapper;

    public NeighborhoodDTO toDTO(Neighborhood neighborhood) {
        if (neighborhood == null) return null;

        NeighborhoodDTO dto = new NeighborhoodDTO();
        dto.setId(neighborhood.getId());
        dto.setName(neighborhood.getName());
        dto.setDescription(neighborhood.getDescription());
        List<Coordinate> activeCoords = neighborhood.getGeolocation()
                .stream()
                .filter(c -> !c.isDeleted())
                .collect(Collectors.toList());

        dto.setGeolocation(coordinateMapper.toDTOList(activeCoords));
        dto.setGeolocation(coordinateMapper.toDTOList(neighborhood.getGeolocation()));
        dto.setDeleted(neighborhood.isDeleted());
        return dto;
    }

    public Neighborhood fromDTO(NeighborhoodDTO dto) {
        if (dto == null) return null;

        Neighborhood neighborhood = new Neighborhood();
        neighborhood.setName(dto.getName());
        neighborhood.setDescription(dto.getDescription());
        neighborhood.setGeolocation(coordinateMapper.fromDTOList(dto.getGeolocation()));
        neighborhood.setDeleted(dto.isDeleted());
        return neighborhood;
    }

    public List<NeighborhoodDTO> toDTOList(List<Neighborhood> neighborhoods) {
        if (neighborhoods == null) return null;
        return neighborhoods.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateFromDTO(NeighborhoodDTO dto, Neighborhood existing) {
        if (dto == null || existing == null) return;

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());

        updateCoordinates(dto, existing);

        existing.setDeleted(dto.isDeleted());
    }

    private void updateCoordinates(NeighborhoodDTO dto, Neighborhood existing) {
        if (dto.getGeolocation() == null) {
            existing.setGeolocation(null);
            return;
        }

        List<Coordinate> currentCoords = existing.getGeolocation();
        List<Coordinate> updatedCoords = new ArrayList<>();
        List<Long> incomingIds = dto.getGeolocation().stream()
                .map(CoordinateDTO::getId)
                .filter(Objects::nonNull)
                .toList();

        for (CoordinateDTO coordDTO : dto.getGeolocation()) {
            if (coordDTO.getId() != null) {
                Coordinate match = currentCoords.stream()
                        .filter(c -> c.getId().equals(coordDTO.getId()))
                        .findFirst()
                        .orElse(null);
                if (match != null) {
                    coordinateMapper.updateFromDTO(coordDTO, match);
                    match.setDeleted(false);
                    updatedCoords.add(match);
                }
            } else {
                Coordinate newCoord = coordinateMapper.fromDTO(coordDTO);
                newCoord.setDeleted(false);
                updatedCoords.add(newCoord);
            }
        }

        for (Coordinate oldCoord : currentCoords) {
            if (!incomingIds.contains(oldCoord.getId())) {
                oldCoord.setDeleted(true);
                updatedCoords.add(oldCoord);
            }
        }

        existing.setGeolocation(updatedCoords);
    }
}