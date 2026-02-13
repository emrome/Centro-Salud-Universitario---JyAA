package mappers;

import dtos.CoordinateDTO;
import models.Coordinate;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.stream.Collectors;
import java.util.List;

@ApplicationScoped
public class CoordinateMapper {

    public CoordinateDTO toDTO(Coordinate coordinate) {
        if (coordinate == null) return null;

        CoordinateDTO dto = new CoordinateDTO();
        dto.setId(coordinate.getId());
        dto.setLat(coordinate.getLatitude());
        dto.setLng(coordinate.getLongitude());
        dto.setDeleted(coordinate.isDeleted());
        return dto;
    }

    public Coordinate fromDTO(CoordinateDTO dto) {
        if (dto == null) return null;

        Coordinate coordinate = new Coordinate();
        coordinate.setId(dto.getId());
        coordinate.setLatitude(dto.getLat());
        coordinate.setLongitude(dto.getLng());
        coordinate.setDeleted(dto.isDeleted());
        return coordinate;
    }

    public List<CoordinateDTO> toDTOList(List<Coordinate> coordinates) {
        if (coordinates == null) return List.of();;
        return coordinates.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Coordinate> fromDTOList(List<CoordinateDTO> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream()
                .map(this::fromDTO)
                .collect(Collectors.toList());
    }

    public void updateFromDTO(CoordinateDTO dto, Coordinate existing) {
        if (dto == null || existing == null) return;
        existing.setLatitude(dto.getLat());
        existing.setLongitude(dto.getLng());
        existing.setDeleted(dto.isDeleted());
    }
}