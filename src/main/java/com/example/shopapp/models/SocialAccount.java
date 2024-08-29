package com.example.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "social_accounts")
@Entity
@Builder
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider",nullable = false,length = 20)
    private String provider;

    @Column(name = "provider_id",length = 50)
    private String providerId;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
