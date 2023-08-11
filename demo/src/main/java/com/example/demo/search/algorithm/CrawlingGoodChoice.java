package com.example.demo.search.algorithm;

import com.example.demo.search.vo.CrawledHotel;
import com.example.demo.search.vo.HotelInfo;
import com.example.demo.search.vo.PriceByDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CrawlingGoodChoice {
    public static void main(String[] args){
//        https://www.goodchoice.kr/product/detail?ano=65814&adcno=2&sel_date=2023-07-28&sel_date2=2023-07-29
//        detail("https://www.goodchoice.kr/product/detail?ano=65814&adcno=2",
//                LocalDate.of(2023, 8, 5),
//                LocalDate.of(2023, 8, 10),
//                2L
//        );

//        search("강릉",
//                LocalDate.of(2023, 8, 5),
//                LocalDate.of(2023, 8, 10),
//                2L
//        );
    }

    @Cacheable(cacheNames = "search_goodChoice", key = "{#keyword, #startDate, #endDate, #day, 'goodChoice'}", sync = true)
    public HashMap<String, CrawledHotel> search(String keyword, LocalDate startDate, LocalDate endDate, Long day) {
        HashMap<String, CrawledHotel> goodChoiceHashMap = new HashMap<>();

        String scoreRegex = "(\\d+\\.\\d+)";
        Pattern scorePattern = Pattern.compile(scoreRegex);

        for (LocalDate i = startDate; i.isBefore(endDate.minusDays(day).plusDays(1).plusDays(1)); i = i.plusDays(1)) {
            try {
                Document document = Jsoup.connect("https://www.goodchoice.kr/product/result?" +
                                "sel_date=" + i.toString() +
                                "&sel_date2=" + i.plusDays(day).minusDays(1).toString() +
                                "&keyword=" + keyword)
                        .get();

                Elements list_4s = document.getElementsByClass("list_4");
                for (Element l : list_4s) {
                    String title = l.getElementsByClass("lazy").attr("alt");

                    if (!goodChoiceHashMap.containsKey(title)) {
                        String href = null;
                        try {
                            URI uri = new URI(l.getElementsByTag("a").attr("href"));
                            href = uri.getScheme() + "://" + uri.getHost() + uri.getPath() + "?" + uri.getQuery().split("&")[0];
                        } catch (URISyntaxException e) {
                        }

                        String img = "https:" + l.getElementsByClass("lazy").attr("data-original");
                        Matcher scoreMatcher = scorePattern.matcher(l.getElementsByClass("score").text());
                        String score = scoreMatcher.find() ? scoreMatcher.group(1) : null;

                        CrawledHotel goodChoice = new CrawledHotel(title);
                        HotelInfo hotelInfo = new HotelInfo("goodChoice", href, img, score);
                        goodChoice.addHotelInfo(hotelInfo);
                        goodChoiceHashMap.put(title, goodChoice);
                    }

                    String price = Optional
                            .ofNullable(l.select("b[style=\"color: rgba(0,0,0,1);\"]").first())
                            .map(Element::text)
                            .map(text -> text.replaceAll("[^0-9]", ""))
                            .orElse(null);
                    CrawledHotel crawledHotel = goodChoiceHashMap.get(title);
                    PriceByDate priceByDate = new PriceByDate("goodChoice", i.toString(), i.plusDays(day).minusDays(1).toString(), price);
                    crawledHotel.addPriceByDate(priceByDate);
                    goodChoiceHashMap.put(title, crawledHotel);
                }

                Set<String> keys = goodChoiceHashMap.keySet();

                // 모든 키와 값을 출력
//                for (String key : keys) {
//                    CrawledHotel value = goodChoiceHashMap.get(key);
//                    System.out.println(value.toString());
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return goodChoiceHashMap;
    }

    public static HashMap<String, List<PriceByDate>> detail(String href, LocalDate startDate, LocalDate endDate, Long day) {
        HashMap<String, List<PriceByDate>> goodChoiceHashMap = new HashMap<>();

        Pattern pattern = Pattern.compile("[0-9,]+");

        //https://www.goodchoice.kr/product/detail?ano=65814&adcno=2&sel_date=2023-07-28&sel_date2=2023-07-29
        for(LocalDate i = startDate; i.isBefore(endDate.minusDays(day).plusDays(1).plusDays(1)); i = i.plusDays(1)) {
            try {
                String url = href + "&sel_date=" + i.toString() + "&sel_date2=" + i.plusDays(day).minusDays(1).toString();

                Document document = Jsoup.connect(url).get();

                Elements elements = document.getElementsByClass("room");
                for (Element l : elements) {
                    String title = l.getElementsByClass("title").text();

                    if (!goodChoiceHashMap.containsKey(title)) {
                        List<PriceByDate> priceByDates = new ArrayList<>();
                        goodChoiceHashMap.put(title, priceByDates);
                    }

                    Matcher matcher = pattern.matcher(l.select("b").text());
                    String price = matcher.find() ? matcher.group().replaceAll(",", "") : null;

                    List<PriceByDate> priceByDates = goodChoiceHashMap.get(title);
                    PriceByDate priceByDate = new PriceByDate("goodChoice", i.toString(), i.plusDays(day).minusDays(1).toString(), price);
                    priceByDates.add(priceByDate);
                    goodChoiceHashMap.put(title, priceByDates);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return goodChoiceHashMap;
    }
}