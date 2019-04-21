package com.semear.tec.palavrizapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String userId;
    private String fullname;
    private String email;
    private String password;
    private String location;
    private UserType userType;
    private Plans plan;
    private String photoUri;


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

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.fullname);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.location);
        dest.writeInt(this.userType == null ? -1 : this.userType.ordinal());
        dest.writeInt(this.plan == null ? -1 : this.plan.ordinal());
        dest.writeString(this.photoUri);
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.fullname = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.location = in.readString();
        int tmpUserType = in.readInt();
        this.userType = tmpUserType == -1 ? null : UserType.values()[tmpUserType];
        int tmpPlan = in.readInt();
        this.plan = tmpPlan == -1 ? null : Plans.values()[tmpPlan];
        this.photoUri = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
