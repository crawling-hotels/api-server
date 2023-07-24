package com.example.demo.search;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlingYanolja {
    public static void main(String[] args) throws Exception {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");

        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisCommands<String, String> syncCommands = connection.sync();


            SafariOptions options = new SafariOptions();
            WebDriver driver = new SafariDriver(new SafariOptions());

            Boolean bootstrap = Boolean.TRUE;
            LocalDate checkinDate = LocalDate.of(2023, 8, 1);
            LocalDate checkoutDate = LocalDate.of(2023, 8, 10);

            String url = "https://www.yanolja.com/search/" +
                    "강릉?keyword=강릉" +
                    "&searchKeyword=강릉" +
                    "&checkinDate=" + checkinDate +
                    "&checkoutDate=" + checkoutDate;

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
                    if(bootstrap){
                        String scoreValue = aTag.findElement(By.cssSelector(".PlaceListScore_rating__3Glxf")).getText();
                        String imageValue = aTag.findElement(By.cssSelector(".PlaceListImage_imageText__2XEMn")).getAttribute("style");
                        String hrefValue = aTag.getAttribute("href");

                        String titleValue = aTag.getAttribute("title");
                        Matcher matcher = pattern.matcher(titleValue);
                        title = matcher.replaceAll("");
                        syncCommands.sadd(title, scoreValue, imageValue, hrefValue);
                    }

                    String titleValue = aTag.getAttribute("title");
                    Matcher matcher = pattern.matcher(titleValue);
                    title = matcher.replaceAll("");

                    String price = "";
                    try {
                        price = aTag.findElement(By.cssSelector(".PlacePriceInfoV2_discountPrice__1PuwK")).getText();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        /**
                         * 예약마감인 경우
                         */
                        price = driver.findElement(By.cssSelector(".PlacePriceInfoV2_priceNote__3xLR2")).getText();
                    }
                    syncCommands.sadd(title, checkinDate.toString() + "{" + price + "}");


//                System.out.println("href: " + hrefValue + ", title: " + title + ", price: " + price + ", score: " + scoreValue);
                    System.out.println("All Members: " + syncCommands.smembers(title));
                }
                bootstrap = Boolean.FALSE;
            }finally {
                driver.quit();
            }
        }

    }


}
