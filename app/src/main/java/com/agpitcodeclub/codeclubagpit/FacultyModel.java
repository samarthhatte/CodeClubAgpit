package com.agpitcodeclub.codeclubagpit;

public class FacultyModel {

    private String name;
    private String role;
    private String imageUrl;

    // REQUIRED empty constructor (Firestore needs this)
    public FacultyModel() {
    }

    public FacultyModel(String name, String role, String imageUrl) {
        this.name = name;
        this.role = role;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

