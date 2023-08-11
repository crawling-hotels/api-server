package com.example.demo.search.algorithm;

import com.example.demo.search.vo.CrawledHotel;
import com.example.demo.search.vo.HotelInfo;
import com.example.demo.search.vo.PriceByDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

@Component
public class CrawledHotelMerge {
    private static int MAX_REQUEST_THREAD_NUM = 3;
    private final CrawlingYanolja crawlingYanolja;
    private final CrawlingGoodChoice crawlingGoodChoice;

    public CrawledHotelMerge(CrawlingYanolja crawlingYanolja, CrawlingGoodChoice crawlingGoodChoice) {
        this.crawlingYanolja = crawlingYanolja;
        this.crawlingGoodChoice = crawlingGoodChoice;
    }

    public static void main(String[] args){
//        String keyword = "광주";
//        LocalDate startDate = LocalDate.of(2023, 8, 1);
//        LocalDate endDate = LocalDate.of(2023, 8, 3);
//        Long day = 2L;
//
//        HashMap<String, CrawledHotel> result = search(keyword, startDate, endDate, day);
//
//        for (String key : result.keySet()) {
//            CrawledHotel value = result.get(key);
//            System.out.println(value.toString());
//        }

        HotelInfo hotelInfo1 = new HotelInfo("1", "2", "3", "4");
        HotelInfo hotelInfo2 = new HotelInfo("1", "2", "3", "4");
        System.out.println(hotelInfo1.equals(hotelInfo2));
        System.out.println(hotelInfo1.hashCode());
        System.out.println(hotelInfo2.hashCode());
    }

    public HashMap<String, CrawledHotel> search(String keyword, LocalDate startDate, LocalDate endDate, Long day){
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            List<Future<HashMap<String, CrawledHotel>>> futures = new ArrayList<>();

            int[] daysSuitableDatesPerThread = assignSuitableDates(startDate, endDate, day);
//            for(int i = 0; i < MAX_REQUEST_THREAD_NUM; i++){
//                System.out.println("daysSuitableDatesPerThread[" + i + "] : " + daysSuitableDatesPerThread[i]);
//            }

            LocalDate tempStartDate = startDate;
            LocalDate tempEndDate = endDate;
            for(int i = 0; i < MAX_REQUEST_THREAD_NUM; i++) {
//                System.out.println(i);
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
                System.out.println("start : " + finalTempStartDate + " end : " + finalTempEndDate);
                futures.add(executorService.submit(() -> crawlingGoodChoice.search(keyword, finalTempStartDate, finalTempEndDate, day)));
                futures.add(executorService.submit(() -> crawlingYanolja.search(keyword, finalTempStartDate, finalTempEndDate, day)));
                tempStartDate = tempEndDate.minusDays(1);
            }

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

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
        //  60일을 최대로 한다고 가정하고 6개의 스레드를 최대로 하자.
        int[] daysSuitableDatesPerThread = new int[MAX_REQUEST_THREAD_NUM];

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long daysToRequest = daysBetween - day + 1;

        for(int i = 0; i < MAX_REQUEST_THREAD_NUM; i++){
//            System.out.println(daysToRequest);
            daysSuitableDatesPerThread[i] = (int) (daysToRequest / MAX_REQUEST_THREAD_NUM);
        }

        for(int i = 0; i < (int) (daysToRequest % MAX_REQUEST_THREAD_NUM); i++){
            daysSuitableDatesPerThread[i] += 1;
        }

        return daysSuitableDatesPerThread;
    }
}
