package com.dqt.security.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/product-redis")

@Slf4j
@RequiredArgsConstructor
public class ProductRedisController {

    private final ProductService productService;

    @PostMapping
    public Product save(@RequestBody Product product) {
        return productService.save(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(key = "#id", value = "product")
    public Product findProduct(@PathVariable int id) {
        return productService.findById(id);
    }
    @DeleteMapping("/{id}")
    @Cacheable(key = "#id", value = "product")
    public String remove(@PathVariable int id)   {
        return productService.delete(id);
    }
}
