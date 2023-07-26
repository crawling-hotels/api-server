package com.example.demo.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

public class CrawlingGoodChoice {
    public static HashMap<String, CrawledHotel> search(String keyword, LocalDate startDate, LocalDate endDate, Long day) {
        HashMap<String, CrawledHotel> goodChoiceHashMap = new HashMap<>();


        for(LocalDate i = startDate; i.isBefore(endDate.minusDays(day).plusDays(1)); i = i.plusDays(1)) {
            try {
                Document document = Jsoup.connect("https://www.goodchoice.kr/product/result?" +
                                "sel_date=" + i.toString() +
                                "&sel_date2=" + i.plusDays(day).toString() +
                                "&keyword=" + keyword)
                        .get();

                Elements list_4s = document.getElementsByClass("list_4");
                for (Element l : list_4s) {
                    String title = l.getElementsByClass("lazy").attr("alt");

                    if (!goodChoiceHashMap.containsKey(title)) {
                        String href = l.getElementsByTag("a").attr("href");
                        String img = l.getElementsByClass("lazy").attr("data-original");
                        String score = l.getElementsByClass("score").text();

                        CrawledHotel goodChoice = new CrawledHotel(title);
                        HotelInfo hotelInfo = new HotelInfo(href, img, score);
                        goodChoice.addHotelInfo(hotelInfo);
                        goodChoiceHashMap.put(title, goodChoice);
                    }

                    String price = l.select("b[style=\"color: rgba(0,0,0,1);\"]").first().text();
                    CrawledHotel crawledHotel = goodChoiceHashMap.get(title);
                    PriceByDate priceByDate = new PriceByDate("goodChoice", i.toString(), i.plusDays(day).toString(), price);
                    crawledHotel.addPriceByDate(priceByDate);
                    goodChoiceHashMap.put(title, crawledHotel);
                }

                Set<String> keys = goodChoiceHashMap.keySet();

                // 모든 키와 값을 출력
//                for (String key : keys) {
//                    CrawledHotel value = goodChoiceHashMap.get(key);
//                    System.out.println(value.toString());
//                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return goodChoiceHashMap;
    }
}