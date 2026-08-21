package com.rental.CarRental.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="car")
public class Car {
    @Id
    private String licensePlate;
    private String brand;
    private int seats;
    private String primaryColor;
    private String secondaryColor;

    @ManyToOne
    @JoinColumn(name="rental_company_id")
    private RentalCompany rentalCompany;

    @OneToMany(mappedBy = "order")
    private List<Order> orders;

    protected Car() {}

    public Car(String licensePlate, String brand, int seats, String primaryColor, String secondaryColor, RentalCompany rentalCompany) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.seats = seats;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.rentalCompany = rentalCompany;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public int getSeats() {
        return seats;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setRentalCompany(RentalCompany rentalCompany) {
        this.rentalCompany = rentalCompany;
    }
}
