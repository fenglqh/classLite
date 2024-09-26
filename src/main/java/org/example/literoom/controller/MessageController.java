package org.example.literoom.controller;

import jakarta.annotation.Resource;
import org.example.literoom.mapper.MessageMapper;
import org.example.literoom.model.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class MessageController {
    @Resource
    private MessageMapper messageMapper;

    @GetMapping("/getHistoryMessage")
    @ResponseBody
    public List<Message> getHistoryMessage(Integer sessionId) {
        List<Message> messages = messageMapper.getMessageBySessionId(sessionId);
        Collections.reverse(messages);
        return messages;
    }
}
