package com.example.shopapp.services.order;

import com.example.shopapp.dtos.CartItemDTO;
import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.*;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    @Transactional
    @Override
    public Order createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        User user=userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()->new DataNotFoundException("User not found"));
        Order order=new Order();
        modelMapper.map(orderDTO,order);
        order.setActive(true);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order=orderRepository.save(order);
        List<OrderDetail> orderDetails=new ArrayList<>();
        float totalMoney= 0;
        for(CartItemDTO cartItemDTO:orderDTO.getCartItems()){
            Product product=productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(()->new DataNotFoundException("Product not found"));
            OrderDetail orderDetail=OrderDetail.builder()
                    .order(order)
                    .product(product)
                    //price này có thể khác so với price trong product vì có thể có discount
                    .price(product.getPrice())
                    .numberOfProducts(cartItemDTO.getQuantity())
                    .totalMoney(cartItemDTO.getQuantity()*product.getPrice())
                    .build();
            orderDetails.add(orderDetail);
            totalMoney+=(cartItemDTO.getQuantity()*product.getPrice());
        }
        order.setTotalMoney(totalMoney);
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOder(Long id) throws DataNotFoundException {
        return orderRepository.findById(id).orElseThrow(()->new DataNotFoundException("Order not found"));
    }

    @Transactional
    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order=orderRepository.findById(id).orElseThrow(()->new DataNotFoundException("Order not found"));
        User user=userRepository.findById(orderDTO.getUserId()).orElseThrow(()->new DataNotFoundException("User not found"));
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        return orderRepository.save(order);
    }
    @Transactional
    @Override
    public void deleteOrder(Long id) throws DataNotFoundException {
        Order order=orderRepository.findById(id).orElseThrow(()->new DataNotFoundException("Order not found"));
        orderRepository.deleteById(id);
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) throws DataNotFoundException {
        User user=userRepository.findById(userId)
                .orElseThrow(()->new DataNotFoundException("User not found"));
        return orderRepository.findByUserId(userId);
    }
    @Transactional
    @Override
    public Order updateOrderActive(Long id, boolean active) throws DataNotFoundException {
        Order order=orderRepository.findById(id)
                .orElseThrow(()->new DataNotFoundException("Order not found"));
        order.setActive(active);
        return orderRepository.save(order);
    }
}
