package com.example.a3yashspc.yash;

/**
 * Created by 3yash's pc on 8/4/2018.
 */

public class Messages
{
    private String message , type;
    private long time ;
    private boolean seen;
    private String from;
    private String body;
    private Integer senderId;

    public Messages()
    {

    }

    public Messages(String message, String type, long time, boolean seen, String from , String body , Integer senderId) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from =from;
        this.body = body;
        this.senderId = senderId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getBody() {
        return body;
    }

    public Object getSenderId() {
        return senderId;
    }
}
