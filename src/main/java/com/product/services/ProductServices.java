package com.product.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.product.DAO.ProductDAO;
import com.product.entity.Product;

import java.util.List;

@Service
public class ProductServices {

    private final ProductDAO productDAO;

    @Autowired
    public ProductServices(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product getProductById(int id) {
        return productDAO.findById((long) id).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public Product saveProduct(Product product) {
        return productDAO.save(product);
    }
}