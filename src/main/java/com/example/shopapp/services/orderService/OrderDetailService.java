package com.example.shopapp.services.orderService;

import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.models.Product;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService{
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;


    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order order=orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Order not found"));
        Product product=productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()->new DataNotFoundException("Product not found"));
        OrderDetail orderDetail=OrderDetailDTO.convertToOrderDetail(orderDetailDTO);
        orderDetail.setProduct(product);
        orderDetail.setOrder(order);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Order Detail not found"));
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) throws DataNotFoundException {
        Order order=orderRepository.findById(orderId)
                .orElseThrow(()->new DataNotFoundException("Order not found"));
        return orderDetailRepository.findByOrderId(order.getId());
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        OrderDetail orderDetail=orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Order Detail not found"));
        Order order=orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(()->new DataNotFoundException("Order not found"));
        Product product=productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(()->new DataNotFoundException("Product not found"));
        orderDetail.setPrice(orderDetailDTO.getPrice());
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        orderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        orderDetail.setColor(orderDetailDTO.getColor());
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) throws DataNotFoundException {
        OrderDetail orderDetail=orderDetailRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Order Detail not found"));
        orderDetailRepository.deleteById(orderDetail.getId());
    }
}
