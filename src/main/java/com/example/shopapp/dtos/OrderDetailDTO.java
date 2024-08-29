package com.example.shopapp.dtos;

import com.example.shopapp.models.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    @Min(value = 1,message = "User's ID must be >0")
    @JsonProperty("order_id")
    private Long orderId;

    @Min(value = 1,message = "User's ID must be >0")
    @JsonProperty("product_id")
    private Long productId;

    @Min(value = 0,message = "price must be >0")
    private float price;

    @Min(value = 1,message = "number_of_products must be >=1")
    @JsonProperty("number_of_products")
    private Integer numberOfProducts;

    @Min(value = 0,message = "total money must be >=0")
    @JsonProperty("total_money")
    private float totalMoney;

    private String color;
    public static OrderDetail convertToOrderDetail(OrderDetailDTO orderDetailDTO){
        return OrderDetail.builder()
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
    }
}
