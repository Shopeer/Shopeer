package com.example.shopeer;

import java.sql.Time;

public class PeerRoomObject {

    private String peerName;
    private String lastMessage;
    private String timeofLM;
    private int peerProfilePic;

    public PeerRoomObject(String peerName, String lastMessage, String timeofLM, int peerProfilePic) {
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.timeofLM = timeofLM;
        this.peerProfilePic = peerProfilePic;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimeofLM() {
        return timeofLM;
    }

    public void setTimeofLM(String timeofLM) {
        this.timeofLM = timeofLM;
    }

    public int getPeerProfilePic() {
        return peerProfilePic;
    }

    public void setPeerProfilePic(int peerProfilePic) {
        this.peerProfilePic = peerProfilePic;
    }
}
