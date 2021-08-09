package com.tavaresrit.petshop.models;

import com.google.firebase.auth.FirebaseAuth;

public class User {
    public String name;
    public String email;
    public String password;

    public User() {
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }



}
