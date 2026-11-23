package com.learn.lostfound.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import com.learn.lostfound.enums.UserEnums;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserEnums.Role role = UserEnums.Role.USER; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserEnums.Status status = UserEnums.Status.ACTIVE;

    public User() {}
}