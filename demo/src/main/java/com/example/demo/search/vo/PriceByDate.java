package com.example.demo.search.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PriceByDate implements Serializable {
    @JsonProperty
    private String company;
    @JsonProperty
    private String checkin;
    @JsonProperty
    private String checkout;
    @JsonProperty
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
