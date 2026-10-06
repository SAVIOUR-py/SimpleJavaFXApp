package com.example.hellofx;

/**
 * Model: one customer. Immutable, so a row never changes behind the table's back.
 * Public getters let PropertyValueFactory("name") and ("province") read the values.
 */
public class Customer {

    private final String name;
    private final String province;

    public Customer(String name, String province) {
        this.name = name;
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    @Override
    public String toString() {
        return name + " (" + province + ")";
    }
}
