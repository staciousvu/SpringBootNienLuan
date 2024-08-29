package com.example.shopapp.repositories;

import com.example.shopapp.models.Category;
import com.example.shopapp.models.OrderDetail;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long id);
}
