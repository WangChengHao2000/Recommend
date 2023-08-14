package com.competition.recommend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private int age;

    private String gender;

    private String occupation;

    private String type;

    private String publickey;

    private String privatekey;

    private String p0;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String type) {
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public User(String username, String password, int age,
                String gender, String occupation, String publickey, String privatekey,String p0) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.occupation = occupation;
        this.publickey = publickey;
        this.privatekey = privatekey;
        this.p0 = p0;

    }
}