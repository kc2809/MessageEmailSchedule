package com.example.administrator.qlda.message.data;

import java.io.Serializable;

/**
 * Created by Administrator on 10/6/2016.
 */
public class EmailAccount implements Serializable{

    private String username;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EmailAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public EmailAccount() {
    }
}
