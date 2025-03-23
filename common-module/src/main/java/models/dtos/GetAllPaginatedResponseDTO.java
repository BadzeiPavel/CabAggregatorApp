package models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPaginatedResponseDTO<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
}
