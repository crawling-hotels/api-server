package com.example.demo.search.vo;

import java.util.ArrayList;
import java.util.List;

public class CrawledHotel {
    private String name;

    private List<HotelInfo> hotelInfos;

    private List<PriceByDate> prices;

    public CrawledHotel(String name) {
        this.name = name;
    }

    public void addHotelInfo(HotelInfo hotelInfo){
        if(hotelInfos == null){
            hotelInfos = new ArrayList<>();
        }

        this.hotelInfos.add(hotelInfo);
    }

    public void addHotelInfoAll(List<HotelInfo> hotelInfos){
        this.hotelInfos.addAll(hotelInfos);
    }

    public List<HotelInfo> getHotelInfos(){
        return hotelInfos;
    }

    public void addPriceByDate(PriceByDate priceByDate){
        if(prices == null){
            prices = new ArrayList<>();
        }

        this.prices.add(priceByDate);
    }

    public void addPriceByDateAll(List<PriceByDate> priceByDates){
        this.prices.addAll(priceByDates);
    }

    public List<PriceByDate> getPrices(){
        return prices;
    }

    @Override
    public String toString() {
        String returnStr =  "name : " + this.name +
                " hotelInfos : " ;
        for(HotelInfo h : hotelInfos){
            returnStr += h.toString();
        }

        returnStr += ", prices : ";
        for(PriceByDate p : prices){
            returnStr += p.toString();
        }

        return returnStr;
    }
}
