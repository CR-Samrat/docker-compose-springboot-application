package com.example.spring_app_with_mysql.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_app_with_mysql.model.Product;
import com.example.spring_app_with_mysql.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return new ResponseEntity<>(this.productRepository.findAll(), HttpStatus.OK);
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        return new ResponseEntity<>(this.productRepository.save(product), HttpStatus.CREATED);
    }
}
