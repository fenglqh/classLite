package org.example.literoom.mapper;

import org.example.literoom.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;
    @Test
    void insert() {
        System.out.println("-----------------"+userMapper);
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        userMapper.insert(user);
    }

    @Test
    void select() {
        userMapper.select("ttt");
    }
}