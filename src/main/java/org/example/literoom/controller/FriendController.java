package org.example.literoom.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.literoom.mapper.FriendMapper;
import org.example.literoom.model.Friend;
import org.example.literoom.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FriendController {
    private static final Logger log = LoggerFactory.getLogger(FriendController.class);
    @Resource
    FriendMapper friendMapper;

    @GetMapping("/getFriendList")
    @ResponseBody
    public Object getFriendList(HttpServletRequest request) {
        // 1. 先从会话中获取到userid。如果获取不到则返回空friend列表
        HttpSession session = request.getSession(false);
        if (session == null) {
//            会话不存在，此时用户未登录
            return new ArrayList<Friend>();
        }
        User user = (User) session.getAttribute("user");
//        此时会话对象不存在
        if (user == null) {
            return new ArrayList<Friend>();
        }
        // 2. 获取到之后，再去查询朋友列表.如果查询出现异常则返回空对象
        List<Friend> friendList;
        try {
            friendList = friendMapper.getFriendList(user.getUserId());
        } catch (Exception e) {
            return new Friend();
        }
        return friendList;
    }
}
