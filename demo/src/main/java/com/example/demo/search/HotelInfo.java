package com.example.demo.search;

public class HotelInfo {
    private String path;
    private String image;
    private String score;

    public HotelInfo(String path, String image, String score) {
        this.path = path;
        this.image = image;
        this.score = score;
    }

    public String toString(){
        return "{ path : " + this.path + ", image : " + this.image + ", score : " + this.score + " } ";
    }


}
