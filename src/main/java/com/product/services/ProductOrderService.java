package com.product.services;

import com.Repository.ProductOrderRepository;
import com.product.entity.ProductOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
