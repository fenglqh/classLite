package org.example.literoom.model;

import lombok.Data;

@Data
public class MessageResponse {
    private String type = "message";
    private Integer fromId;
    private String fromName;
    private Integer sessionId;
    private String content;
}
