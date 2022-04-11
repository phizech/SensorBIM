package com.example.sensorBIM.repository;

import com.example.sensorBIM.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(String email);
}
