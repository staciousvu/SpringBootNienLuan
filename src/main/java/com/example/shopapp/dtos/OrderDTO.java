package com.example.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    @Min(value = 1,message = "User's ID must be >0")
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    private String email;

    @Size(min = 5,message = "Phone number must be at lest 5 characters")
    @NotBlank(message = "PhoneNumber is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    private String note;

//    @Min(value =0,message = "TotalMoney must be>=0")
//    @JsonProperty("total_money")
//    private float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @NotEmpty(message = "Cart items cannot be empty")
    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;

}
