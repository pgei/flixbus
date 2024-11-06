package main.java.model;

import java.util.List;

public class Administrator extends Person {

    private final Boolean admin = true;
    private List<Transport> transports;

    public Administrator (String name, String email, String password) {
        super(name, email, password);
    }

    public Boolean isAdmin() {return this.admin;}

    public List<Transport> getAllAdministeredTransports() {
        return this.transports;
    }

}
