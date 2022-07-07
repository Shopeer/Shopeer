package com.example.shopeer;

public class RoomObject {

    private String roomName;
    private String lastMessage;
    private String timeofLM;
    private int roomProfilePic;

    public RoomObject(String peerName, String lastMessage, String timeofLM, int peerProfilePic) {
        this.roomName = peerName;
        this.lastMessage = lastMessage;
        this.timeofLM = timeofLM;
        this.roomProfilePic = peerProfilePic;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
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

    public int getRoomProfilePic() {
        return roomProfilePic;
    }

    public void setRoomProfilePic(int roomProfilePic) {
        this.roomProfilePic = roomProfilePic;
    }
}
