package com.example.shopapp.repositories;

import com.example.shopapp.models.Category;
import com.example.shopapp.models.Order;
import io.micrometer.common.lang.NonNullApi;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long user_id);
    Page<Order> findAll(Pageable pageable);

}
