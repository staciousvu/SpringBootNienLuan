package com.example.shopapp.responses;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductListResponse {
    private List<ProductResponse> productResponses;
    private int totalPages;
    private Long count;
}
