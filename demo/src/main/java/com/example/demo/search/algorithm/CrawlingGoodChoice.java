package com.example.demo.search.algorithm;

import com.example.demo.search.dto.CrawlingRequest;
import com.example.demo.search.vo.CrawledHotel;
import com.example.demo.search.vo.HotelInfo;
import com.example.demo.search.vo.PriceByDate;
import com.example.demo.util.constant.Constant;
import com.example.demo.util.pool.WebDriverPool;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CrawlingGoodChoice {
    private final Semaphore semaphore = new Semaphore(Constant.CONCURRENT_CRAWLING_NUM);
    private final BlockingQueue<CrawlingRequest> crawlingQueue = new ArrayBlockingQueue<>(Constant.BLOCKING_QUEUE_NUM);

    public void addCrawlingRequest(CrawlingRequest crawlingRequest) {
        try {
            crawlingQueue.put(crawlingRequest);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public <T> HashMap<String, T> crawl() {
        HashMap<String, T> searchResult = new HashMap<>();
        try {
            while (!crawlingQueue.isEmpty()) {
                CrawlingRequest crawlingRequest = crawlingQueue.poll();

                try{
                    semaphore.acquire();

                    switch(crawlingRequest.getCrawlingType()){
                        case SEARCH -> searchResult = search(
                                crawlingRequest.getKeyword(),
                                crawlingRequest.getCheckinDate(),
                                crawlingRequest.getCheckoutDate(),
                                crawlingRequest.getDay()
                        );
                        case DETAIL -> searchResult = detail(
                                crawlingRequest.getKeyword(),
                                crawlingRequest.getCheckinDate(),
                                crawlingRequest.getCheckoutDate(),
                                crawlingRequest.getDay()
                        );
                    }
                }finally {
                    semaphore.release();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return searchResult;
    }

    @Cacheable(cacheNames = "search_goodChoice", key = "{#keyword, #startDate, #endDate, #day, 'goodChoice'}", sync = true)
    public <T> HashMap<String, T> search(String keyword, LocalDate startDate, LocalDate endDate, Long day) {
        HashMap<String, T> goodChoiceHashMap = new HashMap<>();

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
                        goodChoiceHashMap.put(title, (T) goodChoice);
                    }

                    String price = Optional
                            .ofNullable(l.select("b[style=\"color: rgba(0,0,0,1);\"]").first())
                            .map(Element::text)
                            .map(text -> text.replaceAll("[^0-9]", ""))
                            .orElse(null);
                    CrawledHotel crawledHotel = (CrawledHotel) goodChoiceHashMap.get(title);
                    PriceByDate priceByDate = new PriceByDate("goodChoice", i.toString(), i.plusDays(day).minusDays(1).toString(), price);
                    crawledHotel.addPriceByDate(priceByDate);
                    goodChoiceHashMap.put(title, (T) crawledHotel);
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

    public <T> HashMap<String, T> detail(String href, LocalDate startDate, LocalDate endDate, Long day) {
        HashMap<String, T> goodChoiceHashMap = new HashMap<>();

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
                        goodChoiceHashMap.put(title, (T) priceByDates);
                    }

                    Matcher matcher = pattern.matcher(l.select("b").text());
                    String price = matcher.find() ? matcher.group().replaceAll(",", "") : null;

                    List<PriceByDate> priceByDates = (List<PriceByDate>) goodChoiceHashMap.get(title);
                    PriceByDate priceByDate = new PriceByDate("goodChoice", i.toString(), i.plusDays(day).minusDays(1).toString(), price);
                    priceByDates.add(priceByDate);
                    goodChoiceHashMap.put(title, (T) priceByDates);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return goodChoiceHashMap;
    }
}