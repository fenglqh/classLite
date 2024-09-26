package org.example.literoom.model;

import lombok.Data;

@Data
public class Message {
    private Integer messageId;
    private Integer sessionId;
    private Integer fromId;
    private String fromName;
    private String content;
}
