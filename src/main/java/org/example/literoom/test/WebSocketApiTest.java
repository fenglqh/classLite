package org.example.literoom.test;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketApiTest extends TextWebSocketHandler {

    // 在WebSocket连接建立成功后，被自动调用
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("websocket连接建立成功");

    }

    // 在WebSocket收到消息后，被自动调用
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("websocket收到消息 " + message.toString());

    }

    // 在WebSocket连接出现异常后，比如断网，被调用
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

        System.out.println("websocket连接出现异常");
    }

    // 在WebSocket连接被关闭后调用
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("websocket 连接成功关闭");
    }
}
