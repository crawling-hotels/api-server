package com.example.demo.search.service;

import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.domain.HotelDetail;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.search.vo.CrawledHotel;
import com.example.demo.search.algorithm.CrawledHotelMerge;
import com.example.demo.search.vo.HotelInfo;
import com.example.demo.search.vo.PriceByDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private CrawledHotelMerge crawledHotelMerge;

    @Transactional
    public MessageResponse<HashMap<String, CrawledHotel>> search(String keyword, LocalDate startDate, LocalDate endDate, Long day) {
        HashMap<String, CrawledHotel> crawling = crawledHotelMerge.search(keyword, startDate, endDate, day);

        for(String key : crawling.keySet()){
            if(!hotelRepository.existsByName(key)){
                CrawledHotel value = crawling.get(key);

                List<HotelDetail> hotelDetails = new ArrayList<>();

                for(HotelInfo hd: value.getHotelInfos()){
                    hotelDetails.add(new HotelDetail(hd));
                }

                Hotel hotel = new Hotel(key);
                hotel.addHotelDetailAll(hotelDetails);
                hotelRepository.save(hotel);
            }
        }

        return MessageResponse.of(ResponseCodeEnum.HOTEL_SEARCH_SUCCESS, crawling);
    }

    public MessageResponse<HashMap<String, PriceByDate>> detail(String keyword, LocalDate checkinDate, LocalDate checkoutDate, Long day){
//        Hotel hotel = hotelRepository.findByName(name)
//                .orElseThrow(() -> throw new )

        /**
         * HotelService 에서 호출하지 않는 이유 : hotelService.getHotel()은 존재하지 않을때 새로 생성하기 때문에.
         */
        Hotel hotel = hotelRepository.findByName(keyword)
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()));

        List<HotelInfo> hotelInfos =
                hotel.getHotelDetails().stream().map(hotelDetail -> hotelDetail.getHotelInfo()).collect(Collectors.toList());

        HashMap<String, List<PriceByDate>> prices =
                CrawledHotelMerge.detail(hotelInfos, checkinDate, checkoutDate, day);

        return MessageResponse.of(ResponseCodeEnum.DETAIL_SEARCH_SUCCESS, prices);
    }

}
