package com.example.spring_app_with_mysql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.spring_app_with_mysql.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    
}
