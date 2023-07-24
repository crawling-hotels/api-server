package com.example.demo.search;

import java.util.ArrayList;
import java.util.List;

public class CrawledHotel {
    private String name;
    private String path;
    private String image;
    private String score;
    private List<PriceByDate> prices;

    public CrawledHotel(String name, String path, String image, String score) {
        this.name = name;
        this.path = path;
        this.image = image;
        this.score = score;
    }

    public void addPriceByDate(PriceByDate priceByDate){
        if(prices == null){
            prices = new ArrayList<>();
        }

        this.prices.add(priceByDate);
    }

    @Override
    public String toString() {
        return "path : " + this.path + ", image : " + this.image + ", score : " + this.score + ", prices : " + this.prices;
    }
}
