package com.semear.tec.palavrizapp.models;

public class User {

    private String userId;
    private String fullname;
    private String email;
    private String password;
    private String location;
    private UserType userType;
    private Plans plan;


    public User(){}

    public User(String fullname, String email, String password, String location, UserType userType) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.location = location;
        this.userType = userType;
    }


    public User(String fullname, String email, String password, String location, UserType userType, Plans plan) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.location = location;
        this.userType = userType;
        this.plan = plan;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Plans getPlan() {
        return plan;
    }

    public void setPlan(Plans plan) {
        this.plan = plan;
    }
}
