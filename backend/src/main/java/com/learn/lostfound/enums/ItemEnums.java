package com.learn.lostfound.enums;

public final class ItemEnums {
    private ItemEnums(){}

    public enum Type {
        LOST, 
        FOUND
    }

    public enum Status {
        ACTIVE, 
        MATCHED, 
        RESOLVED, 
        CLOSED, 
        REMOVED
    }
    public enum Visibility {
        PUBLIC,
        PRIVATE
    }
    public enum LocationPrecision {
        EXACT,
        APPROXIMATE,
        AREA_ONLY,
        UNKNOWN
    }
}
