package com.example.demo.search.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class HotelInfo {
    private String company;
    private String path;
    private String image;
    private String score;

    public HotelInfo(String company, String path, String image, String score) {
        this.company = company;
        this.path = path;
        this.image = image;
        this.score = score;
    }

    public String toString(){
        return "{ company : " + company + ", path : " + this.path + ", image : " + this.image + ", score : " + this.score + " } ";
    }


}
