package org.example.literoom.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jdk.jfr.Experimental;
import org.example.literoom.component.OnlineUserManger;
import org.example.literoom.mapper.MessageMapper;
import org.example.literoom.mapper.MessageSessionMapper;
import org.example.literoom.model.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;


@Component
public class WebSocketController extends TextWebSocketHandler {
    @Resource
    private OnlineUserManger onlineUserManger;
    @Resource
    private MessageSessionMapper messageSessionMapper;
    @Resource
    private OnlineUserManger getOnlineUserManger;
    @Resource
    private MessageMapper messageMapper;
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println("[websocket]  链接成功！");
        User user = (User) session.getAttributes().get("user");
        if (user == null) return;
        System.out.println("[websocket]  获取userId: " + user.getUserId());
        // 上线
        onlineUserManger.online(user.getUserId(), session);


    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
        System.out.println("[websocket]  接受到消息：{}"+ message.toString());
        // 进行消息的接收，转发和存储
        // 1. 获取到用户信息以备后用
        User user = (User) session.getAttributes().get("user");
        if (user == null) {
            System.out.println("[websocket]  用户未登录，不能进行消息转发！");
            return;
        }
        // 2. 对请求进行解析，将json格式的字符串转化为java对象
        MessageRequst req = objectMapper.readValue((String) message.getPayload(), MessageRequst.class);
        if (!req.getType().equals("message")) {
            System.out.println("[websocket] 类型不匹配！"+ message.getPayload());
            return;
        }
        // 3. 进行消息转发
        transferMessage(user, req);
        // 4. 消息存储到数据库中
        Message msg = new Message();
        msg.setFromId(user.getUserId());
        msg.setSessionId(req.getSessionId());
        msg.setContent(req.getContent());
        messageMapper.addMessage(msg);
        // 同步更新，该会话最后一次提交时间
        messageSessionMapper.updateLastTimeMessageSession(msg);


    }

    private void transferMessage(User fromUser , MessageRequst req) throws JsonProcessingException {
        // a 构造要转发的响应对象 MessageResponse
        MessageResponse response = new MessageResponse();
        response.setFromId(fromUser.getUserId());
        response.setFromName(fromUser.getUserName());
        response.setSessionId(req.getSessionId());
        response.setContent(req.getContent());
        // 要转化成json格式的字符串
        String resJosn  = objectMapper.writeValueAsString(response);
        // b 根据请求中的sessionId 获取到 会话中的所有用户
        List<Friend> friends = messageSessionMapper.getFriendsBySessionId(req.getSessionId(), fromUser.getUserId());
        // c 遍历用户userId， 查询hash表中对应的websocketSession 分别进行转发（包括自己
        Friend myself = new Friend();
        myself.setFriendId(fromUser.getUserId());
        myself.setFriendName(fromUser.getUserName());
        friends.add(myself);
        for (Friend friend : friends) {
            WebSocketSession session = onlineUserManger.getWebSocketSession(friend.getFriendId()) ;
            // 用户未在线则跳过，不发送
            if (session == null) continue;
            try {
                WebSocketMessage webSocketMessage = new TextMessage(resJosn);
                session.sendMessage(webSocketMessage);
            } catch (IOException e) {
                System.out.println("[websocket] 转发出现异常：{}"+ e.getMessage());
            }
        }
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        System.out.println("[websocket]   出现异常：{}"+ exception.getMessage());
        // 下线
        User user = (User) session.getAttributes().get("user");
        if (user == null) return;
        onlineUserManger.offline(user.getUserId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        System.out.println("[websocket]  连接关闭，status：{}"+ status.toString());
        // 下线
        User user = (User) session.getAttributes().get("user");
        if (user == null) return;
        onlineUserManger.offline(user.getUserId(), session);
    }
}
