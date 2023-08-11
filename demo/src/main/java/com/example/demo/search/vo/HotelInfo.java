package com.example.demo.search.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        HotelInfo other = (HotelInfo) obj;

        return Objects.equals(company, other.company) &&
                Objects.equals(path, other.path) &&
                Objects.equals(image, other.image) &&
                Objects.equals(score, other.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(company, path, image, score);
    }
}
