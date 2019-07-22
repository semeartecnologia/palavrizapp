package com.palavrizar.tec.palavrizapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String userId;
    private String fullname;
    private String email;
    private String password;
    private String location;
    private UserType userType;
    private String plan_id;
    private String photoUri;
    private Long registerDate;

    public User(){}

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

    public String getPlan() {
        return plan_id;
    }

    public void setPlan(String plan) {
        this.plan_id = plan;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public Long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Long registerDate) {
        this.registerDate = registerDate;
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
        dest.writeString(this.plan_id);
        dest.writeString(this.photoUri);
        dest.writeValue(this.registerDate);
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.fullname = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.location = in.readString();
        int tmpUserType = in.readInt();
        this.userType = tmpUserType == -1 ? null : UserType.values()[tmpUserType];
        this.plan_id = in.readString();
        this.photoUri = in.readString();
        this.registerDate = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
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
