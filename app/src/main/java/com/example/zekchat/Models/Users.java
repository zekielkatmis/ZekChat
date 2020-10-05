package com.example.zekchat.Models;

public class Users {
    private String profileAbout, profileBirthday, profileEducation, profilePhoto, profileUsername;

    public Users() {
    }

    public Users(String profileAbout, String profileBirthday, String profileEducation, String profilePhoto, String profileUsername) {
        this.profileAbout = profileAbout;
        this.profileBirthday = profileBirthday;
        this.profileEducation = profileEducation;
        this.profilePhoto = profilePhoto;
        this.profileUsername = profileUsername;
    }

    public String getProfileAbout() {
        return profileAbout;
    }

    public void setProfileAbout(String profileAbout) {
        this.profileAbout = profileAbout;
    }

    public String getProfileBirthday() {
        return profileBirthday;
    }

    public void setProfileBirthday(String profileBirthday) {
        this.profileBirthday = profileBirthday;
    }

    public String getProfileEducation() {
        return profileEducation;
    }

    public void setProfileEducation(String profileEducation) {
        this.profileEducation = profileEducation;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getProfileUsername() {
        return profileUsername;
    }

    public void setProfileUsername(String profileUsername) {
        this.profileUsername = profileUsername;
    }

    @Override
    public String toString() {
        return "Users{" +
                "profileAbout='" + profileAbout + '\'' +
                ", profileBirthday='" + profileBirthday + '\'' +
                ", profileEducation='" + profileEducation + '\'' +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", profileUsername='" + profileUsername + '\'' +
                '}';
    }
}
