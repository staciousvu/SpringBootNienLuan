package com.example.shopapp.services.order;

import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws DataNotFoundException;
    Order getOder(Long id) throws DataNotFoundException;
    Order updateOrder(Long id,OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(Long id) throws DataNotFoundException;
    Page<Order> getAllOrders(Pageable pageable);
    List<Order> getOrdersByUserId(Long userId) throws DataNotFoundException;
    Order updateOrderActive(Long id,boolean active) throws DataNotFoundException;
}
