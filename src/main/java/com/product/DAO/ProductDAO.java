package com.product.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import com.product.entity.Product;

public interface ProductDAO extends JpaRepository<Product, Long> {
}