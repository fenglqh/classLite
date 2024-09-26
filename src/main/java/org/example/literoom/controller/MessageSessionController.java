package org.example.literoom.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.literoom.mapper.MessageMapper;
import org.example.literoom.mapper.MessageSessionMapper;
import org.example.literoom.model.Friend;
import org.example.literoom.model.MessageSession;
import org.example.literoom.model.MessageSessionUser;
import org.example.literoom.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class MessageSessionController {
    private static final Logger log = LoggerFactory.getLogger(MessageSessionController.class);
    @Resource
    MessageSessionMapper messageSessionMapper;
    @Resource
    MessageMapper messageMapper;

    /**
     * 获取会话列表，显示会话最后一条消息
     * @param request
     * @return
     */
    @RequestMapping("/getSessionList")
    @ResponseBody
    public Object getMessageSessionList(HttpServletRequest request) {
        // 1. 获取到当前用户的userId
        HttpSession session = request.getSession(false);
        // 此时未登录
        if (session == null) {
            return new ArrayList<MessageSession>();
        }
        User user = (User)session.getAttribute("user");
        if (user == null) {
            log.info("getMessageSessionList-----获取不到user信息");
            return new ArrayList<MessageSession>();
        }
        // 2. 获取到所有会话
        List<Integer> ids = messageSessionMapper.getSessionIdsByUserId(user.getUserId());
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<MessageSession>();
        }
        // 3. 获取到会话里有的用户信息
        List<MessageSession> list = new ArrayList<MessageSession>();
        for (Integer id : ids) {
            MessageSession m = new MessageSession();
            m.setSessionId(id);
            List<Friend> friends = messageSessionMapper.getFriendsBySessionId(id, user.getUserId());
            m.setFriends(friends);
            String lastMessage = messageMapper.getLastMessageBySessionId(id);
            // 如果获取不到最后的消息，则置为空
            if (lastMessage == null) lastMessage = "";
            m.setLastMessage(lastMessage);
            list.add(m);
        }
        return list;
    }

    @PostMapping("/createSession")
    @ResponseBody
    @Transactional
    public Object createSession(HttpServletRequest request, Integer toUserId) {
        log.info("---createSession---toUserId:{}", toUserId);
        HashMap<String,Integer> map = new HashMap<>();
        // 1. 创建新会话
        MessageSession ms = new MessageSession();
        int i = messageSessionMapper.addMessageSession(ms);
        if (i <= 0) {
            log.info("------createSession----创建会话失败");
            return map;
        }
        // 2. 向会话中添加用户1
        MessageSessionUser item1 = new MessageSessionUser();
        User user = (User)request.getSession().getAttribute("user");
        item1.setUserId(user.getUserId());
        item1.setSessionId(ms.getSessionId());
        int i1 = messageSessionMapper.addMessageSessionUserItem(item1);
        if (i1 <= 0) {
            log.info("------createSession----向会话中添加用户1失败");
            return map;
        }
        // 添加用户2
        MessageSessionUser item2 = new MessageSessionUser();
        item2.setUserId(toUserId);
        item2.setSessionId(ms.getSessionId());
        int i2 = messageSessionMapper.addMessageSessionUserItem(item2);
        if (i2 <= 0) {
            log.info("------createSession----向会话中添加用户2失败");
            return map;
        }
        map.put("sessionId", ms.getSessionId());
        return map;
    }
}
