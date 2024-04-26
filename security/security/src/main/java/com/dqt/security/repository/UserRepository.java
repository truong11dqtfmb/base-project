package com.dqt.security.repository;

import com.dqt.security.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("update User u set u.enable = :enable where u.id = :id")
    void updateEnabled(@Param("enable") Integer enable, @Param("id") Integer id);

    @Query("select u from User u where (coalesce(:key, null) is null or u.fullName like concat('%',:key, '%')) and u.enable = 1")
    List<User> filterUser(Pageable pageable, @Param("key") String key);

    @Query("select count(u) from User u where (coalesce(:key, null) is null or u.fullName like concat('%',:key, '%') or u.email like concat('%',:key, '%'))  and u.enable = 1")
    Long countUser(@Param("key") String key);
}

