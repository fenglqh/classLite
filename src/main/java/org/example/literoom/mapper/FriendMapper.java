package org.example.literoom.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.literoom.model.Friend;

import java.util.List;

@Mapper
public interface FriendMapper {
    List<Friend> getFriendList(int userid);
}
