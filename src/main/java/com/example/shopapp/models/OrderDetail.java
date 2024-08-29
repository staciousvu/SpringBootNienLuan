package com.example.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "order_details")
@Entity
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JoinColumn(name = "price",nullable = false)
    private Float price;

    @JoinColumn(name = "number_of_products",nullable = false)
    private Integer numberOfProducts;

    @JoinColumn(name = "total_money",nullable = false)
    private Float totalMoney;

    private String color;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
