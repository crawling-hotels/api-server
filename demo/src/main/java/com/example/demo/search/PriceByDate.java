package com.example.demo.search;

public class PriceByDate {
    private String company;
    private String checkin;
    private String checkout;
    private String price;

    public PriceByDate(String company, String checkin, String checkout, String price) {
        this.company = company;
        this.checkin = checkin;
        this.checkout = checkout;
        this.price = price;
    }

    @Override
    public String toString() {
        return "{ company : " + this.company + " , checkin : " + this.checkin + " , checkout : " + this.checkout + " , price : " + this.price + " }";
    }
}
