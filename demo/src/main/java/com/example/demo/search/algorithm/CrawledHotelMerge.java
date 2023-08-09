package com.example.demo.search.algorithm;

import com.example.demo.search.vo.CrawledHotel;
import com.example.demo.search.vo.HotelInfo;
import com.example.demo.search.vo.PriceByDate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CrawledHotelMerge {
    private static int MAX_REQUEST_THREAD_NUM = 5;

    public static void main(String[] args){
        String keyword = "광주";
        LocalDate startDate = LocalDate.of(2023, 8, 1);
        LocalDate endDate = LocalDate.of(2023, 8, 3);
        Long day = 2L;

        HashMap<String, CrawledHotel> result = search(keyword, startDate, endDate, day);

        for (String key : result.keySet()) {
            CrawledHotel value = result.get(key);
            System.out.println(value.toString());
        }
    }

    public static HashMap<String, CrawledHotel> search(String keyword, LocalDate startDate, LocalDate endDate, Long day){
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            List<Future<HashMap<String, CrawledHotel>>> futures = new ArrayList<>();

            int[] daysSuitableDatesPerThread = assignSuitableDates(startDate, endDate, day);
            for(int i = 0; i < MAX_REQUEST_THREAD_NUM; i++){
                System.out.println("daysSuitableDatesPerThread[" + i + "] : " + daysSuitableDatesPerThread[i]);
            }
//            futures.add(executorService.submit(() -> CrawlingGoodChoice.search(keyword, startDate, endDate, day)));
//            futures.add(executorService.submit(() -> CrawlingGoodChoice.search(keyword, startDate, endDate, day)));
//            futures.add(executorService.submit(() -> CrawlingGoodChoice.search(keyword, startDate, endDate, day)));
//            futures.add(executorService.submit(() -> CrawlingYanolja.search(keyword, startDate, endDate, day)));
//            futures.add(executorService.submit(() -> CrawlingYanolja.search(keyword, startDate, endDate, day)));
//            futures.add(executorService.submit(() -> CrawlingYanolja.search(keyword, startDate, endDate, day)));

            LocalDate tempStartDate = startDate;
            LocalDate tempEndDate = endDate;
            for(int i = 0; i < MAX_REQUEST_THREAD_NUM; i++) {
                if(daysSuitableDatesPerThread[i] == 0)
                    break;
                tempEndDate = tempStartDate.plusDays(day - 1).plusDays(daysSuitableDatesPerThread[i] - 1);
                LocalDate finalTempStartDate = tempStartDate;
                LocalDate finalTempEndDate = tempEndDate;
                System.out.println("start : " + finalTempStartDate + " end : " + finalTempEndDate);
                futures.add(executorService.submit(() -> CrawlingGoodChoice.search(keyword, finalTempStartDate, finalTempEndDate, day)));
                futures.add(executorService.submit(() -> CrawlingYanolja.search(keyword, finalTempStartDate, finalTempEndDate, day)));
                tempStartDate = tempEndDate.minusDays(1);
            }

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

            HashMap<String, CrawledHotel> mergedHashMap = new HashMap<>();
            for (Future<HashMap<String, CrawledHotel>> future : futures) {
                HashMap<String, CrawledHotel> resultHashMap = future.get();
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

    public static HashMap<String, List<PriceByDate>> detail(List<HotelInfo> hotelInfos, LocalDate checkinDate, LocalDate checkoutDate, Long day){
        try {
            ExecutorService executorService = Executors.newScheduledThreadPool(2);

            List<Future<HashMap<String, List<PriceByDate>>>> futures = new ArrayList<>();

            for(HotelInfo hi: hotelInfos){
                switch (hi.getCompany()){
                    case "yanolja":
                        futures.add(executorService.submit(() ->
                                CrawlingYanolja.detail(hi.getPath(), checkinDate, checkoutDate, day)));
                        break;
                    case "goodChoice":
                        futures.add(executorService.submit(() ->
                                CrawlingGoodChoice.detail(hi.getPath(), checkinDate, checkoutDate, day)));
                        break;
                }
            }

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
                List<HotelInfo> hotelInfos = resultHashMap.get(entry.getKey()).getHotelInfos();

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
        //  60일을 최대로 한다고 가정하고 6개의 스레드를 최대로 하자.
        int[] daysSuitableDatesPerThread = new int[MAX_REQUEST_THREAD_NUM];

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long daysToRequest = daysBetween - day + 1;

        for(int i = 0; i < MAX_REQUEST_THREAD_NUM; i++){
            daysSuitableDatesPerThread[i] = (int) (daysToRequest / MAX_REQUEST_THREAD_NUM);
        }

        for(int i = 0; i < (int) (daysToRequest % MAX_REQUEST_THREAD_NUM); i++){
            daysSuitableDatesPerThread[i] += 1;
        }

        return daysSuitableDatesPerThread;
    }
}
