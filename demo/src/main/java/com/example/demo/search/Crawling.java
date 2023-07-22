package com.example.demo.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.logging.Logger;

public class Crawling {
    private final static Logger log = Logger.getGlobal();

    public static void main(String[] args) throws IOException {


        LocalDate test = LocalDate.now();
        System.out.println(test.toString());

        String decoded = new String(URLDecoder.decode("%EA%B0%95%EB%A6%89", "UTF-8"));
        log.info(decoded);

        Document doc = Jsoup.connect("https://www.goodchoice.kr/product/result?sel_date=2023-07-27&sel_date2=2023-07-28&keyword=%EA%B0%95%EB%A6%89").get();
//        log.info(doc.title());

        Element body = doc.body();
//        System.out.println(body);

        Elements prrr = doc.getElementsByClass("list_4");
        String[] hrefs = new String[prrr.size()];

        CrawledHotel[] crawledHotels = new CrawledHotel[prrr.size()];
        for (int i = 0; i < prrr.size(); i++) {
//            System.out.println(prrr.get(i).select("a").attr("href"));
//            crawledHotels[i] = new CrawledHotel();
//            crawledHotels[i].setAhref(ahrefs.get(i).attr("href"));
            hrefs[i] = prrr.get(i).select("a").attr("href");

        }

        System.out.println("list_4 size is : " + prrr.size());
//        for (Element r: prrr){
//            System.out.println(r.select("a").attr("href"));
//        }

        Elements products = doc.getElementsByClass("list_4 adcno2");
//        for (Element p : products) {
//            System.out.println(p);
//        }

        System.out.println("crawledHotels size is : " + crawledHotels.length);

//        Elements ahrefs = doc.getElementsByClass("list_4 adcno2").select("a");
//        String[] hrefs = new String[ahrefs.size()];
//        for (int i = 0; i < ahrefs.size(); i++) {
////            System.out.println(ahrefs.get(i).attr("href"));
////            crawledHotels[i] = new CrawledHotel();
////            crawledHotels[i].setAhref(ahrefs.get(i).attr("href"));
//            hrefs[i] = ahrefs.get(i).attr("href");
//
//        }

        /**
         * 특급 SL 호텔 강릉 9.0 추천해요 (781) 주문진터미널 차량 4분 7월 깜짝특전! [패밀리풀 2인 입장권 무료]
         */
        Elements names = doc.getElementsByClass("name");
        Elements buildBadgeElements = doc.select("span.build_badge");
        Elements prices = doc.getElementsByClass("price");
//        System.out.println(buildBadgeElements);
        for (Element element : buildBadgeElements) {
            element.remove();
        }

//        Elements badge = doc.getElementsByClass("badge");
        for (Element p : names) {
//            System.out.println(p.getElementsByTag("span").remove().text());
//            String badgeStr = p.getElementsByClass("badge").text();
//            System.out.println(badgeStr);
//            System.out.println(p.select("strong").text());
//            System.out.println(p.getElementsByClass("score").text());

        }
        System.out.println("names size is : " + names.size());
        for(int i = 0; i < names.size(); i++){

            crawledHotels[i] = new CrawledHotel(names.get(i).select("strong").text(), hrefs[i], names.get(i).getElementsByClass("score").text());
//            crawledHotels[i].setName(names.get(i).select("strong").text());
//            crawledHotels[i].setScore(names.get(i).getElementsByClass("score").text());
        }

        for(int i =0; i < crawledHotels.length; i++){
            System.out.println(crawledHotels[i].getName());
        }
//        Elements prices = doc.getElementsByClass("price");
//        for (Element p : prices) {
//            System.out.println(p.text());
//        }

        /**
         * 닫기 슈페리어 헐리우드 (노오션뷰 / Room Only) / / 가격 352,000 319,000원 객실 이용 안내 예약
         */
        Document docc = Jsoup.connect("https://www.goodchoice.kr/product/detail?ano=48951&adcno=2&sel_date=2023-07-27&sel_date2=2023-07-28").get();
        Elements productss = docc.getElementsByClass("room");
//        for (Element p : productss) {
//            System.out.println(p.text());
//        }
    }
}
