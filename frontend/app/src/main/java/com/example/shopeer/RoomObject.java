package com.example.shopeer;

public class RoomObject {

    private int roomId;
    private String roomName;
    private String lastMessage;
    private String timeofLM;
    private int roomProfilePic;

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public RoomObject(int roomId, String roomName, String lastMessage, String timeofLM, int roomProfilePic) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.lastMessage = lastMessage;
        this.timeofLM = timeofLM;
        this.roomProfilePic = roomProfilePic;
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
