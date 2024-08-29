package com.example.shopapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "orders")
@Entity
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fullname",length = 100)
    private String fullName;

    @Column(name = "email",length = 100)
    private String email;

    @Column(name = "phone_number",nullable = false,length = 10)
    private String phoneNumber;


    @Column(name = "address",length = 100)
    private String address;

    @Column(name = "note",length = 100)
    private String note;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
    @Column(name = "status")
    private String status;

    @Column(name = "total_money")
    private float totalMoney;

    @Column(name ="shipping_method")
    private String shippingMethod;

    @Column(name ="shipping_address")
    private String shippingAddress;

    @Column(name ="payment_method")
    private String paymentMethod;

    @Column(name ="shipping_date")
    private Date shippingDate;

    @Column(name ="tracking_number")
    private String trackingNumber;

    @Column(name ="active")
    private boolean active;

//    @ManyToOne
//    @JoinColumn(name = "coupon_id")
//    private Coupon coupon;
}
