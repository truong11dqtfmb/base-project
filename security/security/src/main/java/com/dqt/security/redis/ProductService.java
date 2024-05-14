package com.dqt.security.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final RedisTemplate redisTemplate;

    private static final String PRODUCT_KEY = "product";

    public Product save(Product product) {
        redisTemplate.opsForHash().put(PRODUCT_KEY, product.getId(), product);
        return product;
    }

    public List<Product> findAll() {
        return redisTemplate.opsForHash().values(PRODUCT_KEY);
    }

    public Product findById(Integer id) {
        return (Product) redisTemplate.opsForHash().get(PRODUCT_KEY, id);
    }

    public String delete(Integer id) {
        redisTemplate.opsForHash().delete(PRODUCT_KEY, id);
        return "Remove OKE";
    }



}
