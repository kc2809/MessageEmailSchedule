package com.example.administrator.qlda.message.data;

import java.io.Serializable;

public class MessageData implements Serializable {
    String to=null;

    String subject= null;
    String message= null;

    String displayName =null;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public MessageData(String to,  String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public MessageData(String to, String subject, String message, String displayName) {
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.displayName = displayName;
    }

    public MessageData() {
    }
}
