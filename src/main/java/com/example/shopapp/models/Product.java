package com.example.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "products")
@Entity
@Builder
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",nullable = false,length = 350)
    private String name;

    private float price;

    @Column(name = "thumbnail",length = 350)
    private String thumbnail="";

    @Column(name = "description",length = 350)
    private String description;

    @JoinColumn(name = "category_id")
    @ManyToOne
    private Category category;

}
