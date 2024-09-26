package org.example.literoom.component;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;

// 维护userid和websocket session的映射关系，也就是管理在线用户
@Component
public class OnlineUserManger {
//  涉及到线程安全
    private ConcurrentHashMap<Integer, WebSocketSession> sessions = new ConcurrentHashMap<>();

//    用户上线 向hash表中添加数据
    public void online(Integer userId, WebSocketSession session) {
//        防止不同客户端，同一个账号多开
//        让另一个客户端下线
        if (sessions.get(userId) != null) {
            System.out.println("["+userId+"] 已经在线！");
            return;
        }
        sessions.put(userId, session);
        System.out.println("["+userId+"] 登录成功！");
    }

//    用户下线 向hash表中删除数据
    public void offline(Integer userId, WebSocketSession session) {
        WebSocketSession exitSession = sessions.get(userId);
//        防多开
        if (exitSession == session) {
            sessions.remove(userId);
            System.out.println("["+userId+"] 下线成功！");
        }
    }

//    根据userId 获取websocket session
    public WebSocketSession getWebSocketSession(Integer userId) {
        return sessions.get(userId);
    }
}
