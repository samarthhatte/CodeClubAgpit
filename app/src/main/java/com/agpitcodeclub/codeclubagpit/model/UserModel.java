package com.agpitcodeclub.codeclubagpit.model;

import java.util.List;

public class UserModel {
    private String name;
    private String role;
    private String githubLink;
    private List<String> skills;
    private String profilePic;
    private String github;
    private String linkedin;
    private String email;
    private String portfolio;


    // Empty constructor required for Firebase Firestore
    public UserModel() {}

    public UserModel(String name, String role, String githubLink, List<String> skills, String profilePic) {
        this.name = name;
        this.role = role;
        this.githubLink = githubLink;
        this.skills = skills;
        this.profilePic = profilePic;
    }

    // Getters
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getGithubLink() { return githubLink; }
    public List<String> getSkills() { return skills; }
    public String getProfilePic() { return profilePic; }
    public String getGithub() { return github; }
    public String getLinkedin() { return linkedin; }
    public String getEmail() { return email; }
    public String getPortfolio() { return portfolio; }


    // Setters (Required if you want to modify objects locally before saving)
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public void setGithub(String github) { this.github = github; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
    public void setEmail(String email) { this.email = email; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
}