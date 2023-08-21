package com.example.demo.util.pool;

import com.example.demo.util.constant.Constant;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
@Component
public class WebDriverPool {
    private final BlockingQueue<WebDriver> webDriverQueue = new ArrayBlockingQueue<>(Constant.DRIVER_POOL_NUM);

    public WebDriverPool() {
        for (int i = 0; i < Constant.DRIVER_POOL_NUM; i++) {
            ChromeOptions options = new ChromeOptions();
//            options.setBrowserVersion("116");
//            options.addArguments("headless");
//            options.addArguments("window-size=1920x1080");
//            options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
            WebDriver driver = new ChromeDriver(options);

            webDriverQueue.add(driver);
        }
    }

    public WebDriver acquire() {
        return webDriverQueue.poll();
    }

    public void release(WebDriver webDriver) {
        if (webDriver != null) {
            webDriverQueue.add(webDriver);
        }
    }

    public void close() {
        for (WebDriver webDriver : webDriverQueue) {
            webDriver.quit();
        }
    }
}
