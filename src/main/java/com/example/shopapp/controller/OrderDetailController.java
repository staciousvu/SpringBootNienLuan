package com.example.shopapp.controller;

import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.responses.OrderDetailResponse;
import com.example.shopapp.services.orderService.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    @PostMapping("")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO newOrderDetail, BindingResult result){
        try {
            if (result.hasErrors()){
                List<FieldError> errorList=result.getFieldErrors();
                List<String> list=errorList.stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(list);
            }
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetailService.createOrderDetail(newOrderDetail)));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable("id") Long id){
        try {
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetailService.getOderDetail(id)));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") Long orderId){
        try {
            List<OrderDetailResponse> orderDetailResponses=new ArrayList<>();
            List<OrderDetail> orderDetails=orderDetailService.getOrderDetailsByOrderId(orderId);
            for (OrderDetail orderDetail:orderDetails){
                orderDetailResponses.add(OrderDetailResponse.fromOrderDetail(orderDetail));
            }
            return ResponseEntity.ok(orderDetailResponses);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> updateOrderDetail(@PathVariable Long id
            ,@Valid @RequestBody OrderDetailDTO newOrderDetailDTO
            ,BindingResult result){
        try {
            if (result.hasErrors()){
                List<FieldError> errorList=result.getFieldErrors();
                List<String> list=errorList.stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(list);
            }
            OrderDetailResponse orderDetailResponse=OrderDetailResponse
                    .fromOrderDetail(orderDetailService.updateOrderDetail(id,newOrderDetailDTO));
            return ResponseEntity.ok(orderDetailResponse);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable Long id){
        try {
            orderDetailService.deleteOrderDetail(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
