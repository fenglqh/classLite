package org.example.literoom.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.literoom.model.Friend;
import org.example.literoom.model.Message;
import org.example.literoom.model.MessageSession;
import org.example.literoom.model.MessageSessionUser;

import java.util.List;

@Mapper
public interface MessageSessionMapper {

    // 根据当前userid，获取到含有该用户的所有sessionId，并根据最后一次发送消息的时间进行降序排序
    List<Integer> getSessionIdsByUserId(Integer userId);

    // 根据当前sessionId，获取该会话中含有的所有用户,并排除自己
    List<Friend> getFriendsBySessionId(Integer sessionId, Integer selfUserId);

    //    创建一个新的会话，并将数据库的自增主键sessionId放入到MessageSession的属性中
    int addMessageSession(MessageSession messageSession);

    //    添加会话中的成员
    int addMessageSessionUserItem(MessageSessionUser messageSessionUser);

    // 更新会话中的时间
    void updateLastTimeMessageSession(Message message);
}
