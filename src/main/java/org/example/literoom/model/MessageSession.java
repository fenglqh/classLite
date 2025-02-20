package org.example.literoom.model;

import lombok.Data;

import java.util.List;
@Data
public class MessageSession {
    private Integer sessionId;
    private List<Friend> friends;
    private String lastMessage;
}
