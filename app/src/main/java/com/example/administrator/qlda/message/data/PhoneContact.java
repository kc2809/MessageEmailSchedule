package com.example.administrator.qlda.message.data;

/**
 * Created by Administrator on 10/10/2016.
 */
public class PhoneContact {
    String displayName;
    String phoneNumber;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneContact(String displayName, String phoneNumber) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    public PhoneContact(String displayName, String phoneNumber, int id) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

    public PhoneContact() {
    }

    @Override
    public String toString() {
        return displayName + "\n["+ phoneNumber +"]";
    }
}
