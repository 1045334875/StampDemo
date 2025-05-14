package com.example.demo.service;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User createUser() {
        String userId = UUID.randomUUID().toString();
        String userName = "default name"; // 默认用户名

        // 保存用户到数据库
        // 这里假设你有一个JdbcTemplate来操作数据库
        String sql = "INSERT INTO Users (userId, userName) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, userName);

        return new User(userId, userName);
    }
}