package com.example.demo.search.algorithm;

import com.example.demo.search.dto.CrawlingRequest;
import com.example.demo.search.vo.CrawledHotel;
import com.example.demo.search.vo.CrawlingType;
import com.example.demo.search.vo.HotelInfo;
import com.example.demo.search.vo.PriceByDate;
import com.example.demo.util.constant.Constant;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

@Component
public class CrawledHotelMerge {
    private final CrawlingYanolja crawlingYanolja;
    private final CrawlingGoodChoice crawlingGoodChoice;

    public CrawledHotelMerge(CrawlingYanolja crawlingYanolja, CrawlingGoodChoice crawlingGoodChoice) {
        this.crawlingYanolja = crawlingYanolja;
        this.crawlingGoodChoice = crawlingGoodChoice;
    }

    public <T> HashMap<String, CrawledHotel> search(CrawlingRequest crawlingRequest){
        String keyword = crawlingRequest.getKeyword();
        LocalDate startDate = crawlingRequest.getCheckinDate();
        LocalDate endDate = crawlingRequest.getCheckoutDate();
        Long day = crawlingRequest.getDay();

        try {
            long threadSeparateStart = System.nanoTime();
            ExecutorService executorService = Executors.newCachedThreadPool();

            List<Future<HashMap<String, T>>> futures = new ArrayList<>();

            int[] daysSuitableDatesPerThread = assignSuitableDates(
                    startDate, endDate, day
            );

//            for(int i = 0; i < MAX_REQUEST_THREAD_NUM; i++){
//                System.out.println("daysSuitableDatesPerThread[" + i + "] : " + daysSuitableDatesPerThread[i]);
//            }

            LocalDate tempStartDate = startDate;
            LocalDate tempEndDate = endDate;
            for(int i = 0; i < Constant.TASK_SEPARATE_NUM; i++) {
                if(daysSuitableDatesPerThread[i] == 0)
                    continue;
                /**
                 * 1 2 3 4 5 6 7 , day : 3, thread : 3
                 * 1 2 3 4 startday : 1 + day : 3 - 1 = 3. 3 + daySuitableDatesPerThread[i] : 2 - 1 = 4
                 * 3 4 5 6 startday : 4 - 1 = 3.
                 * 5 6 7
                 */
                tempEndDate = tempStartDate.plusDays(day - 1).plusDays(daysSuitableDatesPerThread[i] - 1);

                LocalDate finalTempStartDate = tempStartDate;
                LocalDate finalTempEndDate = tempEndDate;
                crawlingYanolja.addCrawlingRequest(new CrawlingRequest(keyword, finalTempStartDate, finalTempEndDate, day, CrawlingType.SEARCH));
                crawlingGoodChoice.addCrawlingRequest(new CrawlingRequest(keyword, finalTempStartDate, finalTempEndDate, day, CrawlingType.SEARCH));

                tempStartDate = tempEndDate.minusDays(1);
            }

            /**
             * 비동기로 받아오기.
             */
            futures.add(executorService.submit(() -> crawlingGoodChoice.crawl()));
            futures.add(executorService.submit(() -> crawlingYanolja.crawl()));

            long threadSeparateEnd = System.nanoTime();

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            HashMap<String, CrawledHotel> mergedHashMap = new HashMap<>();
            for (Future<HashMap<String, T>> future : futures) {
                HashMap<String, CrawledHotel> resultHashMap = (HashMap<String, CrawledHotel>) future.get();
                mergeSearchHashMaps(mergedHashMap, resultHashMap);
            }

            return mergedHashMap;
        }catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public HashMap<String, List<PriceByDate>> detail(List<HotelInfo> hotelInfos, LocalDate checkinDate, LocalDate checkoutDate, Long day){
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            List<Future<HashMap<String, List<PriceByDate>>>> futures = new ArrayList<>();

            for(HotelInfo hi: hotelInfos){
                switch (hi.getCompany()){
                    case "yanolja":
                        crawlingYanolja.addCrawlingRequest(new CrawlingRequest(hi.getPath(), checkinDate, checkoutDate, day, CrawlingType.DETAIL));
                        break;
                    case "goodChoice":
                        crawlingGoodChoice.addCrawlingRequest(new CrawlingRequest(hi.getPath(), checkinDate, checkoutDate, day, CrawlingType.DETAIL));
                        break;
                }
            }

            /**
             * 비동기로 받아오기.
             */
            futures.add(executorService.submit(() -> crawlingGoodChoice.crawl()));
            futures.add(executorService.submit(() -> crawlingYanolja.crawl()));

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            HashMap<String, List<PriceByDate>> mergedHashMap = new HashMap<>();
            for (Future<HashMap<String, List<PriceByDate>>> future : futures) {
                HashMap<String, List<PriceByDate>> resultHashMap = future.get();
                mergeDetailHashMaps(mergedHashMap, resultHashMap);
            }

            return mergedHashMap;
        }catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void mergeSearchHashMaps(HashMap<String, CrawledHotel> mergedHashMap, HashMap<String, CrawledHotel> resultHashMap){

        for(Map.Entry<String, CrawledHotel> entry: resultHashMap.entrySet()) {
            if(mergedHashMap.containsKey(entry.getKey())){
                List<PriceByDate> prices = resultHashMap.get(entry.getKey()).getPrices();
                Set<HotelInfo> hotelInfos = resultHashMap.get(entry.getKey()).getHotelInfos();

                CrawledHotel value = mergedHashMap.get(entry.getKey());
                value.addHotelInfoAll(hotelInfos);
                value.addPriceByDateAll(prices);

                mergedHashMap.put(entry.getKey(), value);

            } else {
                mergedHashMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static void mergeDetailHashMaps(HashMap<String, List<PriceByDate>> mergedHashMap, HashMap<String, List<PriceByDate>> resultHashMap){

        for(Map.Entry<String, List<PriceByDate>> entry: resultHashMap.entrySet()) {
            if(mergedHashMap.containsKey(entry.getKey())){
                List<PriceByDate> value = mergedHashMap.get(entry.getKey());
                List<PriceByDate> prices = resultHashMap.get(entry.getKey());
                value.addAll(prices);

                mergedHashMap.put(entry.getKey(), value);

            } else {
                List<PriceByDate> value = new ArrayList<>();
                value.addAll(entry.getValue());
                mergedHashMap.put(entry.getKey(), value);
            }
        }
    }

    private static int[] assignSuitableDates(LocalDate startDate, LocalDate endDate, Long day){
        int[] daysSuitableDatesPerThread = new int[Constant.TASK_SEPARATE_NUM];

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long numToRequest = daysBetween - day + 1;

        for(int i = 0; i < Constant.TASK_SEPARATE_NUM; i++){
//            System.out.println(daysToRequest);
            daysSuitableDatesPerThread[i] = (int) (numToRequest / Constant.TASK_SEPARATE_NUM);
        }

        for(int i = 0; i < (int) (numToRequest % Constant.TASK_SEPARATE_NUM); i++){
            daysSuitableDatesPerThread[i] += 1;
        }

        return daysSuitableDatesPerThread;
    }
}
