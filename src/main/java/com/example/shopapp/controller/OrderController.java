package com.example.shopapp.controller;

import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.models.Order;
import com.example.shopapp.services.order.IOrderService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    @PostMapping("")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult result){
        try {
            if (result.hasErrors()){
                List<FieldError> errorList=result.getFieldErrors();
                List<String> list=errorList.stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(list);
            }
            Order order=orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/user/{user_id}")
    public ResponseEntity<?> getOrdersByUserId(@Valid @PathVariable("user_id") Long userId){
        try {
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Long id){
        try {
            return ResponseEntity.ok(orderService.getOder(id));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Long id
            ,@Valid @RequestBody OrderDTO orderDTO
            ,BindingResult result){
        try {
            if (result.hasErrors()){
                List<FieldError> errorList=result.getFieldErrors();
                List<String> list=errorList.stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(list);
            }
            return ResponseEntity.ok(orderService.updateOrder(id,orderDTO));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable("id") Long id){
        //xoa mem active=false
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/active/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateOrderActive(
            @PathVariable Long id,
            @RequestParam boolean active) {
        try {
            Order updatedOrder = orderService.updateOrderActive(id, active);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/generateFakeData")
    public ResponseEntity<String> createProductData() {
        Faker faker=new Faker();
        for (int i=0;i<10000;i++){
//            String name=faker.commerce().productName();
//            if (productService.existByName(name)){
//                continue;
//            }
            OrderDTO orderDTO=OrderDTO.builder()
                    .userId((long) faker.number().numberBetween(8,12))
                    .fullName(faker.name().fullName())
                    .phoneNumber(String.valueOf(faker.number().numberBetween(1000000,10000000)))
                    .address(faker.address().fullAddress())
                    .email(faker.internet().emailAddress())
                    .note(faker.lorem().sentence())
                    .build();
            try {
                orderService.createOrder(orderDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Create Fake Data Successful");
    }
}
