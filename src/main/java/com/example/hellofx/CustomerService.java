package com.example.hellofx;

import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Service layer. For this lab the data lives in memory, so closing the app loses it.
 * The same ObservableList is kept for the whole session: the TableView listens to it,
 * so adding or removing here updates the table automatically.
 *
 * A database version would replace these list operations with a DAO call and
 * report success only after that call succeeds.
 */
public class CustomerService {

    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    public ObservableList<Customer> getCustomers() {
        return customers;
    }

    public void add(Customer customer) {
        customers.add(Objects.requireNonNull(customer, "customer"));
    }

    public void remove(Customer customer) {
        customers.remove(customer);
    }
}
