package com.example.demo.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlingYanolja {
    public static void main(String[] args) throws Exception {
        SafariOptions options = new SafariOptions();
        WebDriver driver = new SafariDriver(new SafariOptions());

        String url = "https://www.yanolja.com/search/강릉?keyword=강릉&searchKeyword=강릉&checkinDate=2023-08-05&checkoutDate=2023-08-11";

        try {
            driver.get(url);
            // WebDriverWait를 사용하여 최대 10초까지 기다립니다.
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 모든 <a> 태그를 가져옵니다.
//            List<WebElement> aTags = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".SunnyLayout_bodyContainer__1wWjV a")));
//            List<WebElement> aTags = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".PlaceListBody_placeListBodyContainer__1u70R a")));

            List<WebElement> aTags = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.PlaceListBody_listGroup__LddQf.PlaceListBody_keywordAssociation__RBXqI a")));

//            List<WebElement> aTags = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".PlaceListBody_itemGroup__1V8Q3 PlaceListBody_textUnitList__HEDb3 a")));

            // 괄호와 그 안의 문자를 제거하는 정규표현식
            String regex = "\\([^\\(\\)]+\\)";

            // 정규표현식에 해당하는 패턴 생성
            Pattern pattern = Pattern.compile(regex);



            // 각 <a> 태그의 href 속성 값을 출력합니다.
            for (WebElement aTag : aTags) {
                String price = "";
                try {
                    price = aTag.findElement(By.cssSelector(".PlacePriceInfoV2_discountPrice__1PuwK")).getText();
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    /**
                     * 예약마감인 경우
                     */
                    price = driver.findElement(By.cssSelector(".PlacePriceInfoV2_priceNote__3xLR2")).getText();
                }
                String scoreValue = aTag.findElement(By.cssSelector(".PlaceListScore_rating__3Glxf")).getText();
                String hrefValue = aTag.getAttribute("href");
                String titleValue = aTag.getAttribute("title");
                // 주어진 문자열에서 정규표현식에 매칭되는 부분 찾기
                Matcher matcher = pattern.matcher(titleValue);
                // 매칭된 부분을 제거하여 결과 문자열 생성
                String title = matcher.replaceAll("");
                System.out.println("href: " + hrefValue + ", title: " + title + ", price: " + price + ", score: " + scoreValue);

            }
        }finally {
            driver.quit();
        }
    }
}
