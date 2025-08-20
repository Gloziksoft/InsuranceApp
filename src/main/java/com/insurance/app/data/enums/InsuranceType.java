package com.insurance.app.data.enums;

/**
 * Types of insurance policies.
 */
public enum InsuranceType {
    PROPERTY("Majetok"),
    APARTMENT("Byt"),
    HOUSE("Dom"),
    CAR("Auto"),
    HEALTH("Zdravie"),
    LIFE("Životné poistenie"),
    TRAVEL("Cestovné poistenie"),
    LIABILITY("Poistenie zodpovednosti"),
    ACCIDENT("Úrazové poistenie"),
    OTHER("Iné");

    private final String displayName;

    InsuranceType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of the insurance type.
     */
    public String getDisplayName() {
        return displayName;
    }
}

