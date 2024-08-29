package com.example.shopapp;

import com.example.shopapp.components.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopappApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopappApplication.class, args);
    }

}
