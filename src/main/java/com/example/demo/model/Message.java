package com.example.demo.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.text.SimpleDateFormat;

@Document(collection = "Messages")
public class Message {
    private String from;
    private String content;
    private String timestamp;

    public Message(String from, String content) {

    }

    public void setTimestamp() {
        this.timestamp = new SimpleDateFormat("HH:mm,MM-dd").format(new Date());
    }

    public String getFrom() {
        return this.from;
    }

    public String getContent() {
        return this.content;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
}
