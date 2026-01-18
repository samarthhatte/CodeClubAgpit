package com.agpitcodeclub.codeclubagpit;

import java.util.List;

public class UserModel {
    private String name;
    private String role;
    private String githubLink;
    private List<String> skills;
    private String profileImageUrl;

    // Empty constructor required for Firebase Firestore
    public UserModel() {}

    public UserModel(String name, String role, String githubLink, List<String> skills) {
        this.name = name;
        this.role = role;
        this.githubLink = githubLink;
        this.skills = skills;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getGithubLink() { return githubLink; }
    public List<String> getSkills() { return skills; }
    public String getProfileImageUrl() { return profileImageUrl; }
}