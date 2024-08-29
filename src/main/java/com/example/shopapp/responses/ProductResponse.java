package com.example.shopapp.responses;

import com.example.shopapp.models.Product;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductResponse extends BaseResponse{
    private Long id;
    private String name;
    private Float price;
    private String thumbnail="";
    private String description;
    private Long categoryId;
    public static ProductResponse convertToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .price(product.getPrice())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .thumbnail(product.getThumbnail())
                .build();
    }
}
