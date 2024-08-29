package com.example.shopapp.responses;

import com.example.shopapp.models.Category;
import com.example.shopapp.models.Product;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    public static CategoryResponse convertToCategoryResponse(Category category){
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
