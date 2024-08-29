package com.example.shopapp.responses;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryListResponse {
    private List<CategoryResponse> categoryResponses;
    private int totalPages;
    private Long count;
}
