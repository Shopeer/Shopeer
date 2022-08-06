package com.example.shopeer.rooms.chat;

public class ChatObject {

    String text;
    String senderEmail;
    long timestamp;
    String currenttime;

    public ChatObject(String text, String senderEmail, long timestamp, String currenttime) {
        this.text = text;
        this.senderEmail = senderEmail;
        this.timestamp = timestamp;
        this.currenttime = currenttime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
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
