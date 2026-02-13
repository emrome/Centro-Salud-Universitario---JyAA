package mappers;

import dtos.CoordinateDTO;
import dtos.ZoneDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.Coordinate;
import models.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class ZoneMapper {

    @Inject
    private CoordinateMapper coordinateMapper;

    public ZoneDTO toDTO(Zone zone) {
        if (zone == null) return null;

        ZoneDTO dto = new ZoneDTO();
        dto.setId(zone.getId());
        dto.setDescription(zone.getDescription());
        dto.setName(zone.getName());
        dto.setNeighborhoodId(zone.getNeighborhood() != null ? zone.getNeighborhood().getId() : null);
        List<Coordinate> activeCoords = zone.getCoordinates()
                .stream()
                .filter(c -> !c.isDeleted())
                .collect(Collectors.toList());

        dto.setCoordinates(coordinateMapper.toDTOList(activeCoords));
        dto.setDeleted(zone.isDeleted());
        return dto;
    }

    public Zone fromDTO(ZoneDTO dto) {
        if (dto == null) return null;

        Zone zone = new Zone();
        zone.setName(dto.getName());
        zone.setDescription(dto.getDescription());
        zone.setCoordinates(coordinateMapper.fromDTOList(dto.getCoordinates()));
        zone.setDeleted(dto.isDeleted());
        return zone;
    }

    public List<ZoneDTO> toDTOList(List<Zone> zones) {
        if (zones == null) return null;
        return zones.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void updateFromDTO(ZoneDTO dto, Zone existing) {
        if (dto == null || existing == null) return;

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        List<Coordinate> currentCoords = existing.getCoordinates();
        List<Coordinate> updatedCoords = new ArrayList<>();

        List<Long> incomingIds = dto.getCoordinates().stream()
                .map(CoordinateDTO::getId)
                .filter(Objects::nonNull)
                .toList();

        for (CoordinateDTO coordDTO : dto.getCoordinates()) {
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

        existing.setCoordinates(updatedCoords);
        existing.setDeleted(dto.isDeleted());
    }
}