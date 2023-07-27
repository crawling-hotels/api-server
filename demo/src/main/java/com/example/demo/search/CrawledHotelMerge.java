package com.example.demo.search;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CrawledHotelMerge {

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
            ExecutorService executorService = Executors.newFixedThreadPool(2);

            List<Future<HashMap<String, CrawledHotel>>> futures = new ArrayList<>();

            futures.add(executorService.submit(() -> CrawlingGoodChoice.search(keyword, startDate, endDate, day)));
            futures.add(executorService.submit(() -> CrawlingYanolja.search(keyword, startDate, endDate, day)));

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            HashMap<String, CrawledHotel> mergedHashMap = new HashMap<>();
            for (Future<HashMap<String, CrawledHotel>> future : futures) {
                HashMap<String, CrawledHotel> resultHashMap = future.get();
                mergeHashMaps(mergedHashMap, resultHashMap);
            }

            return mergedHashMap;
        }catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void mergeHashMaps(HashMap<String, CrawledHotel> mergedHashMap, HashMap<String, CrawledHotel> resultHashMap){

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
}
