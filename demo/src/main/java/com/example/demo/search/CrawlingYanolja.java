package com.example.demo.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlingYanolja {
    public static HashMap<String, CrawledHotel> search(String keyword, LocalDate startDate, LocalDate endDate, Long day) throws Exception {
        HashMap<String, CrawledHotel> yanoljaHashMap = new HashMap<>();

        SafariOptions options = new SafariOptions();
        WebDriver driver = new SafariDriver(new SafariOptions());

        for(LocalDate i = startDate; i.isBefore(endDate.minusDays(day).plusDays(1)); i = i.plusDays(1)) {


            String url = "https://www.yanolja.com/search/" + keyword +
                    "?keyword=" + keyword +
                    "&searchKeyword=" + keyword +
                    "&checkinDate=" + i +
                    "&checkoutDate=" + i.plusDays(day);

            try {
                driver.get(url);
                // WebDriverWait를 사용하여 최대 10초까지 기다립니다.
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                List<WebElement> aTags = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.PlaceListBody_listGroup__LddQf.PlaceListBody_keywordAssociation__RBXqI a")));

                /**
                 * 정규표현식을 통해 괄호 + 안의 내용 제거
                 * 이유 : 명확한 호텔 이름
                 */
                String regex = "\\([^\\(\\)]+\\)";
                Pattern pattern = Pattern.compile(regex);
                String title = "";

                for (WebElement aTag : aTags) {
                    String titleValue = aTag.getAttribute("title");
                    Matcher matcher = pattern.matcher(titleValue);
                    title = matcher.replaceAll("");

                    if (!yanoljaHashMap.containsKey(title)) {
                        String scoreValue = aTag.findElement(By.cssSelector(".PlaceListScore_rating__3Glxf")).getText();
                        String imageValue = aTag.findElement(By.cssSelector(".PlaceListImage_imageText__2XEMn")).getAttribute("style");
                        String hrefValue = aTag.getAttribute("href");

                        CrawledHotel yanolja = new CrawledHotel(title);
                        HotelInfo hotelInfo = new HotelInfo(hrefValue, imageValue, scoreValue);
                        yanolja.addHotelInfo(hotelInfo);
                        yanoljaHashMap.put(title, yanolja);
                    }

                    String price = "";
                    try {
                        price = aTag.findElement(By.cssSelector(".PlacePriceInfoV2_discountPrice__1PuwK")).getText();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        /**
                         * 예약마감인 경우
                         */
                        price = driver.findElement(By.cssSelector(".PlacePriceInfoV2_priceNote__3xLR2")).getText();
                    }

                    CrawledHotel crawledHotel = yanoljaHashMap.get(title);
                    PriceByDate priceByDate = new PriceByDate("yanolja", i.toString(), i.plusDays(day).toString(), price);
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
}
