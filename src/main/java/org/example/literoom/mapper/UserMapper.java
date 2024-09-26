package org.example.literoom.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.literoom.model.User;

@Mapper
public interface UserMapper {

    int insert(User user);

    User select(String username);
}
