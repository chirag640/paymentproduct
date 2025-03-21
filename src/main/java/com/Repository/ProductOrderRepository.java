package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.product.entity.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
}