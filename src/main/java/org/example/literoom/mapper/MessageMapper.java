package org.example.literoom.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.literoom.model.Message;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 获取会话中的最后一条消息
     * @param sessionId
     * @return
     */
    String getLastMessageBySessionId(Integer sessionId);

    /**
     * 获取会话中的最近100条消息
     * @param sessionId
     * @return
     */
    List<Message> getMessageBySessionId(Integer sessionId);

    /**
     * 向message表中插入数据
     * @param message
     * @return
     */
    void addMessage(Message message);
}
