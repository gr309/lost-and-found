package com.learn.lostfound.enums;

public final class UserEnums {
    private UserEnums(){}

    public enum Role {
        USER,
        MODERATOR,
        ADMIN   
    }

    public enum Status {
        ACTIVE,
        SUSPENDED,
        DISABLED    
    }
}
