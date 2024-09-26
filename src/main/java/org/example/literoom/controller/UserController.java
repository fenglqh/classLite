package org.example.literoom.controller;

import jakarta.annotation.Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.literoom.mapper.UserMapper;
import org.example.literoom.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Resource
    UserMapper userMapper;

    @PostMapping("/login")
    @ResponseBody
    public Object login(String username, String password, HttpServletRequest request) {
        // 1. 先检查数据库中是否存在该用户名  再检查密码是否匹配
        User user = userMapper.select(username);
        // 都不匹配则登录失败
        if (user == null || !user.getPassword().equals(password)) {
            return new User();
        }
        // 2. 登录成功则创建一个新会话
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        user.setPassword("");
        return user;
    }

    @PostMapping("/register")
    @ResponseBody
    public Object register(String username, String password) {
        User user = null;
        try {
            user = new User();
            user.setUserName(username);
            user.setPassword(password);
            int insert = userMapper.insert(user);
            System.out.println("insert : " + insert);
        } catch (DuplicateKeyException e) {
            // 如果抛出异常则表明，用户名重复，注册失败
            user = new User();
        }

        return user;
    }

    @RequestMapping("/userinfo")
    @ResponseBody
    public Object getUserInfo(HttpServletRequest request) {
        log.info("---------getUserInfo- start--------");
        HttpSession session = request.getSession(false);
        if (session == null) {
//            此时用户未登录
            return new User();
        }
        User user = (User) session.getAttribute("user");
        if (user == null) {
            log.info("[getUserInfo]获取不到user信息");
            return new User();
        }
        user.setPassword("");
        return user;
    }
}
