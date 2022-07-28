package com.example.shopeer;

public class ProfileObject {

    private String email;
    private String name;
    private String description;
    private String photo;

    public ProfileObject(String email, String name, String description, String photo) {
        this.email = email;
        this.name = name;
        this.description = description;
        this.photo = photo;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return this.photo;
    }
}
