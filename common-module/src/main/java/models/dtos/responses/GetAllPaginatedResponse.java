package models.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPaginatedResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
}
