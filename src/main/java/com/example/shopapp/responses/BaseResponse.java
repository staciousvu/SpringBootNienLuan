package com.example.shopapp.responses;

import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
