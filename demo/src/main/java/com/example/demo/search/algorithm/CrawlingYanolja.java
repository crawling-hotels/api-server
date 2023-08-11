package com.example.demo.search.algorithm;

import com.example.demo.search.vo.CrawledHotel;
import com.example.demo.search.vo.HotelInfo;
import com.example.demo.search.vo.PriceByDate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CrawlingYanolja {
    private CacheManager cacheManager;

    @Autowired
    public CrawlingYanolja(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public static void main(String[] args){
        detail("https://place-site.yanolja.com/places/25886",
                LocalDate.of(2023, 8, 5),
                LocalDate.of(2023, 8, 10),
                2L
        );
    }

    @Cacheable(value = "search_yanolja", key = "{#keyword, #startDate, #endDate, #day, 'yanolja'}", sync = true)
    public HashMap<String, CrawledHotel> search(String keyword, LocalDate startDate, LocalDate endDate, Long day) throws Exception {
        HashMap<String, CrawledHotel> yanoljaHashMap = new HashMap<>();

//        SafariOptions options = new SafariOptions();
//        WebDriver driver = new SafariDriver(new SafariOptions());
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBrowserVersion("115");
        WebDriver driver = new ChromeDriver(chromeOptions);

        String titleRegex = "\\([^\\(\\)]+\\)";
        Pattern titlePattern = Pattern.compile(titleRegex);

        String imageRegex = "background-image:\\\\s*url\\\\(\\\"([^\\\"]+)\\\"\\\\);";
        Pattern imagePattern = Pattern.compile(imageRegex);

        for(LocalDate i = startDate; i.isBefore(endDate.minusDays(day).plusDays(1).plusDays(1)); i = i.plusDays(1)) {

            String url = "https://www.yanolja.com/search/" + keyword +
                    "?keyword=" + keyword +
                    "&searchKeyword=" + keyword +
                    "&checkinDate=" + i +
                    "&checkoutDate=" + i.plusDays(day).minusDays(1);

            try {
                driver.get(url);
                // WebDriverWait를 사용하여 최대 10초까지 기다립니다.
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

//                List<WebElement> aTags = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.PlaceListBody_listGroup__LddQf.PlaceListBody_keywordAssociation__RBXqI a")));
                List<WebElement> aTags = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.PlaceListItemText_container__fUIgA.text-unit a")));



                String title = "";

                for (WebElement aTag : aTags) {
                    String titleValue = aTag.getAttribute("title");
                    Matcher matcher = titlePattern.matcher(titleValue);
                    title = matcher.replaceAll("");

                    if (!yanoljaHashMap.containsKey(title)) {
                        String scoreValue = aTag.findElement(By.cssSelector(".PlaceListScore_rating__3Glxf")).getText();
//                        String imageValue = imagePattern.matcher(aTag.findElement(By.cssSelector(".PlaceListImage_imageText__2XEMn")).getAttribute("style")).group(1);
                        String imageValue = aTag
                                .findElement(By.cssSelector(".PlaceListImage_imageText__2XEMn"))
                                .getAttribute("style")
                                .replaceAll("background-image:\\s*url\\(\"", "")
                                .replaceAll("\\\"\\);", "");
                        String hrefValue = aTag.getAttribute("href");

                        CrawledHotel yanolja = new CrawledHotel(title);
                        HotelInfo hotelInfo = new HotelInfo("yanolja", hrefValue, imageValue, scoreValue);
                        yanolja.addHotelInfo(hotelInfo);
                        yanoljaHashMap.put(title, yanolja);
                    }

                    String price = "";
                    try {
                        price = aTag
                                .findElement(By.cssSelector(".PlacePriceInfoV2_discountPrice__1PuwK"))
                                .getText()
                                .replaceAll("[^0-9]", "");
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        /**
                         * 예약마감인 경우
                         */
//                        price = driver.findElement(By.cssSelector(".PlacePriceInfoV2_priceNote__3xLR2")).getText();
                        price = null;
                    }

                    CrawledHotel crawledHotel = yanoljaHashMap.get(title);
                    PriceByDate priceByDate = new PriceByDate("yanolja", i.toString(), i.plusDays(day).minusDays(1).toString(), price);
                    crawledHotel.addPriceByDate(priceByDate);
                    yanoljaHashMap.put(title, crawledHotel);
                }

                Set<String> keys = yanoljaHashMap.keySet();

                // 모든 키와 값을 출력
//                for (String key : keys) {
//                    CrawledHotel value = yanoljaHashMap.get(key);
//                    System.out.println(value.toString());
//                }
            } finally {
//                driver.quit();
            }
        }
        driver.quit();

        return yanoljaHashMap;
    }

    public static HashMap<String, List<PriceByDate>> detail(String href, LocalDate startDate, LocalDate endDate, Long day){
        /**
         * key : 방 이름, value : PriceByDate
         * 1. keyword 로 Hotel 디비에서 hotelInfos ahref 찾기
         * 2. 찾아서 detail페이지에서 요청날리기
         * 3. 방별로 구분해서 가격 그래프 보여주기
         */
        HashMap<String, List<PriceByDate>> yanoljaHashMap = new HashMap<>();

        Pattern pattern = Pattern.compile("[0-9,]+");

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBrowserVersion("115");
        WebDriver driver = new ChromeDriver(chromeOptions);

        for(LocalDate i = startDate; i.isBefore(endDate.minusDays(day).plusDays(1).plusDays(1)); i = i.plusDays(1)) {
            String url = href + "?checkinDate=" + i.toString() + "&checkoutDate=" + i.plusDays(day).minusDays(1).toString() + "&adultPax=2";

            try {
                driver.get(url);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                List<WebElement> webElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.className("css-d7p7g4")));

                for (WebElement w : webElements) {
                    //room
                    String room = w.findElement(By.cssSelector(".css-1ux2lue")).getText();

                    if (!yanoljaHashMap.containsKey(room)) {
                        List<PriceByDate> priceByDates = new ArrayList<>();
                        yanoljaHashMap.put(room, priceByDates);
                    }

                    Matcher matcher = pattern.matcher(w.findElement(By.cssSelector(".css-17ymi7c")).getText());
                    String price = matcher.find() ? matcher.group().replaceAll(",", "") : null;


                    List<PriceByDate> priceByDates = yanoljaHashMap.get(room);
                    PriceByDate priceByDate = new PriceByDate("yanolja", i.toString(), i.plusDays(day).minusDays(1).toString(), price);
                    priceByDates.add(priceByDate);
                    yanoljaHashMap.put(room, priceByDates);
                }

                Set<String> keys = yanoljaHashMap.keySet();

                // 모든 키와 값을 출력
                for (String key : keys) {
                    List<PriceByDate> value = yanoljaHashMap.get(key);
                    String result = key + " ";
                    for(PriceByDate p : value){
                        result += p.toString();
                    }
                    System.out.println(result);
                }
            } finally {
//                driver.quit();
            }
        }
        driver.quit();
        return yanoljaHashMap;
    }
}
