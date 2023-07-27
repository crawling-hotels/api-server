package com.example.demo.search.service;

import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.domain.HotelDetail;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.search.CrawledHotel;
import com.example.demo.search.CrawledHotelMerge;
import com.example.demo.search.HotelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    @Autowired
    private HotelRepository hotelRepository;

    @Transactional
    public MessageResponse<HashMap<String, CrawledHotel>> search(String keyword, LocalDate startDate, LocalDate endDate, Long day) {
        HashMap<String, CrawledHotel> crawling = CrawledHotelMerge.search(keyword, startDate, endDate, day);

        for(String key : crawling.keySet()){
            if(!hotelRepository.existsByName(key)){
                CrawledHotel value = crawling.get(key);

                List<HotelDetail> hotelDetails = new ArrayList<>();

                for(HotelInfo hd: value.getHotelInfos()){
                    hotelDetails.add(new HotelDetail(hd.getCompany(), hd.getPath(), hd.getImage(), hd.getScore()));
                }

                Hotel hotel = new Hotel(key);
                hotel.addHotelDetailAll(hotelDetails);
                hotelRepository.save(hotel);
            }
        }

        return MessageResponse.of(ResponseCodeEnum.HOTEL_SEARCH_SUCCESS, crawling);
    }

//    public MessageResponse<?> detail(String name){
//        System.out.println("test");
//
//        return MessageResponse.of(ResponseCodeEnum.)
//    }

}
