package com.product;

import com.product.DAO.ProductDAO;
import com.product.entity.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.product")
@EnableJpaRepositories(basePackages = {"com.product.DAO", "com.Repository"})
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(ProductDAO productRepository) {
        return args -> {
            if (productRepository.count() == 0) {  // Avoid duplicate entries
                productRepository.save(new Product("Laptop", "High-performance laptop", 1, 75.0, "laptop.jpg"));
                productRepository.save(new Product( "Phone", "Latest smartphone", 2, 50.0, "phone.jpg"));
                productRepository.save(new Product( "Headphones", "Noise-canceling headphones", 1, 20.0, "headphones.jpg"));
                productRepository.save(new Product("Smartwatch", "Feature-packed smartwatch", 3, 80.0, "watch.jpg"));
            }
        };
    }
}