package com.example.hellofx;

/**
 * Validation rules, kept apart from the UI so they are easy to test.
 * Each method returns a friendly message when the input is invalid, or null when it is fine.
 */
public final class CustomerValidator {

    public static final int MAX_NAME_LENGTH = 60;

    private CustomerValidator() {
    }

    /** A required name must contain some text once spaces at the ends are removed. */
    public static String checkName(String rawName) {
        String name = rawName == null ? "" : rawName.trim();
        if (name.isEmpty()) {
            return "Enter the customer name.";
        }
        if (name.length() > MAX_NAME_LENGTH) {
            return "Customer name must be " + MAX_NAME_LENGTH + " characters or fewer.";
        }
        return null;
    }

    /** A province must have a selected value. */
    public static String checkProvince(String province) {
        if (province == null || province.isBlank()) {
            return "Choose a province.";
        }
        return null;
    }
}
