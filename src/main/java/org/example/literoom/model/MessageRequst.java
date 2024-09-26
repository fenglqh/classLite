package org.example.literoom.model;

import lombok.Data;

@Data
public class MessageRequst {
    private String type = "message";
    private Integer sessionId;
    private String content;

}
