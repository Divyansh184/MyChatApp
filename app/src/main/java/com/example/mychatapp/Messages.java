package com.example.mychatapp;

public class Messages {

    String message;
    String sendid;
    long timestamp;
    String currenttime;

    public Messages() {
    }

    public Messages(String message, String sendid, long timestamp, String currenttime) {
        this.message = message;
        this.sendid = sendid;
        this.timestamp = timestamp;
        this.currenttime = currenttime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendid() {
        return sendid;
    }

    public void setSendid(String sendid) {
        this.sendid = sendid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrenttime() {
        return currenttime;
    }

    public void setCurrenttime(String currenttime) {
        this.currenttime = currenttime;
    }
}
