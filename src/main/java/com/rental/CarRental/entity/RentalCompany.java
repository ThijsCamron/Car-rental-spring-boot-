package com.rental.CarRental.entity;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="rental_company")
public class RentalCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "rentalCompany")
    private Set<Car> fleet;

    @OneToMany(mappedBy = "rentalCompany")
    private Set<Admin> admins;

    protected RentalCompany() {}

    public RentalCompany(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
