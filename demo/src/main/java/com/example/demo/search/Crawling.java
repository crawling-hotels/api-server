package com.example.demo.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.logging.Logger;

public class Crawling {
    private final static Logger log = Logger.getGlobal();

    public static void search(String keyword, LocalDate startDate, LocalDate endDate, Long day) throws IOException {

        String encoded = URLEncoder.encode(keyword, "UTF-8");

        for(LocalDate i = startDate; i.isBefore(endDate.minusDays(day + 1)); i.plusDays(1)){
            Document goodChoice = Jsoup.connect("https://www.goodchoice.kr/product/result?" +
                    "sel_date=" + i.toString() +
                    "&sel_date2=" + i.plusDays(day).toString() +
                    "&keyword=" + encoded).get();

            System.out.println(goodChoice.title());

            /**
             * 특급 SL 호텔 강릉 9.0 추천해요 (781) 주문진터미널 차량 4분 7월 깜짝특전! [패밀리풀 2인 입장권 무료]
             */
//            Elements names = goodChoice.getElementsByClass("name");
//            for (Element p : names) {
//                System.out.println(p.text());
//            }
        }

    }
    public static void main2(String[] args) throws IOException {
        String keyword = "강릉";
        LocalDate startDate = LocalDate.of(2023, 8, 1);
        LocalDate endDate = LocalDate.of(2023, 8, 4);
        Long day = 2L;

        for(LocalDate i = startDate; i.isBefore(endDate.minusDays(day).plusDays(2)); i = i.plusDays(1)) {
            System.out.println(i);
            Document goodChoice = Jsoup.connect("https://www.goodchoice.kr/product/result?" +
                    "sel_date=" + i.toString() +
                    "&sel_date2=" + i.plusDays(day).toString() +
                    "&keyword=" + keyword)
                    .get();

            System.out.println(goodChoice.title());
        }
    }

    /**
     * 네이버호텔
     */
    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://hotels.naver.com/list?placeFileName=place%3AJeju_Province&adultCnt=2&checkIn=2023-08-08&checkOut=2023-08-10&includeTax=false&sortField=popularityKR&sortDirection=descending").get();

        System.out.println(doc.body());
    }


    /**
     * 여기어떄
     */
    public static void main3(String[] args) throws IOException {
        /**
         * 2023-07-20
         */
//        LocalDate test = LocalDate.now();
//        System.out.println(test.toString());

        Document doc = Jsoup.connect("https://www.goodchoice.kr/product/result?sel_date=2023-07-27&sel_date2=2023-07-28&keyword=" + "서울").get();
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
