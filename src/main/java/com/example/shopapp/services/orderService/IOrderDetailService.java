package com.example.shopapp.services.orderService;

import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException;
    OrderDetail getOderDetail(Long id) throws DataNotFoundException;
    List<OrderDetail> getOrderDetailsByOrderId(Long orderId) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id,OrderDetailDTO orderDetailDTO) throws DataNotFoundException;
    void deleteOrderDetail(Long id) throws DataNotFoundException;
}
