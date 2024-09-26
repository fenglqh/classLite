package org.example.literoom.config;

import jakarta.annotation.Resource;
import org.example.literoom.controller.WebSocketController;
import org.example.literoom.test.WebSocketApiTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
// 启动WebSocket
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Resource
    private WebSocketApiTest webSocketApiTest;
    @Resource
    private WebSocketController webSocketController;
    // 这个方法，用来将创建好的Handler类注册到具体路径上
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 当浏览器，websocket请求路径是"/webSocker"时，就会调用WebSocketApiTest
        registry.addHandler(webSocketApiTest, "/websocket");
        registry.addHandler(webSocketController,"/message")
                // 在注册handler时， 添加一个特定的拦截器
                // 添加HTTP session拦截器，就可以将httpsession中的存储的键值对，拷贝到websocket session里
                .addInterceptors(new HttpSessionHandshakeInterceptor() );
    }
}
