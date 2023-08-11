package com.example.demo.search.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrawledHotel {
    private String name;

    private Set<HotelInfo> hotelInfos;

    private List<PriceByDate> prices;

    public CrawledHotel(String name) {
        this.name = name;
    }

    public void addHotelInfo(HotelInfo hotelInfo){
        if(hotelInfos == null){
            hotelInfos = new HashSet<>();
        }

        this.hotelInfos.add(hotelInfo);
    }

    public void addHotelInfoAll(Set<HotelInfo> hotelInfos){

        this.hotelInfos.addAll(hotelInfos);
    }

    public Set<HotelInfo> getHotelInfos(){
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
