package com.product.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Repository.ProductOrderRepository;
import com.product.entity.ProductOrder;

import java.util.List;

@Service
public class ProductOrderService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    public List<ProductOrder> getAllOrders() {
        return productOrderRepository.findAll();
    }

    public ProductOrder saveOrder(ProductOrder order) {
        return productOrderRepository.save(order);
    }
}