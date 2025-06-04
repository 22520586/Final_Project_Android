package com.example.final_project.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String fullname;
    private String username;
    private String email;
    private String phone;
    private String password;

    @SerializedName("accessToken")
    private String token;

    // Constructor
    public User(String fullname, String username, String email, String password, String phone) {
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return fullname; }
    public void setName(String name) { this.fullname = name; }
    public String getEmail() { return email; }
    public String getUsername() {return username;}
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}
}
